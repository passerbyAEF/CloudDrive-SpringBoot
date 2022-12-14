package com.clouddrive.gateway.filter;

import com.clouddrive.gateway.balancer.FileServiceUrlBalancer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_LOADBALANCER_RESPONSE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
public class FileServiceUrlFilter implements GlobalFilter, Ordered {

    @Autowired
    LoadBalancerClientFactory clientFactory;

    @Autowired
    LoadBalancerProperties loadBalancerProperties;

    @Override
    public int getOrder() {
        return 10150;
    }

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!"fs".equals(url.getScheme()) && !"fs".equals(schemePrefix))) {
            return chain.filter(exchange);
        }
        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);
        String host = url.getHost();
        String workId = getWorkId(exchange.getRequest());
        if (!StringUtils.hasLength(workId)) {
            return Return503(exchange);
        }
        return choose(host, workId).doOnNext((response) -> {
            URI uri = exchange.getRequest().getURI();
            String overrideScheme = null;
            if (schemePrefix != null) {
                overrideScheme = url.getScheme();
            }
            DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(response.getServer(), overrideScheme);
            URI requestUrl = reconstructURI(serviceInstance, uri);

            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
            exchange.getAttributes().put(GATEWAY_LOADBALANCER_RESPONSE_ATTR, response);
        }).then(chain.filter(exchange));
    }

    private Mono<Response<ServiceInstance>> choose(String serviceId, String workId) {
        FileServiceUrlBalancer balancer = new FileServiceUrlBalancer(serviceId, workId, this.clientFactory.getLazyProvider(serviceId, ServiceInstanceListSupplier.class));
        return balancer.choose();
    }

    protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
        return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
    }

    String getWorkId(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        MultiValueMap<String, String> headers = request.getHeaders();
        if (headers.containsKey("nodeId")) {
            return headers.get("nodeId").get(0);
        }
        if (queryParams.containsKey("nodeId")) {
            return queryParams.get("nodeId").get(0);
        }
        return null;
    }

    Mono<Void> Return503(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        return exchange.mutate().response(response).build().getResponse().setComplete();
    }
}
