//package com.clouddrive.gateway.supplier;
//
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.loadbalancer.Request;
//import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
//import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import reactor.core.publisher.Flux;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class FileServiceInstanceListSupplier implements ServiceInstanceListSupplier {
//
//    private final ServiceInstanceListSupplier delegate;
//
//    public FileServiceInstanceListSupplier(ServiceInstanceListSupplier delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    public String getServiceId() {
//        return null;
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get(Request request) {
//        return delegate.get().map((serviceInstances) -> {
//            List<ServiceInstance> filteredInstances = new ArrayList<>();
//            for (ServiceInstance serviceInstance : serviceInstances) {
//                Map<String, String> metadata = serviceInstance.getMetadata();
//                if (metadata.containsKey("workId")) {
//                    String workId = metadata.get("workId");
//                    if (workId.equals(paramsWorkId)) {
//                        String newPath = String.format("/%s:%s/%s", item.getIp(), item.getPort(), url.getPath());
//                        ServerHttpRequest newRequest = exchange.getRequest().mutate().path(newPath).build();
//                        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
//                        exchange = exchange.mutate().request(newRequest).build();
//                        return chain.filter(exchange);
//                    }
//                }
//                filteredInstances.add(serviceInstance);
//            }
//            //如果没找到就返回空列表，绝不返回其他集群的实例
//            return filteredInstances;
//        });
//    }
//
//    @Override
//    public Flux<List<ServiceInstance>> get() {
//        return null;
//    }
//}
