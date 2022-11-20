package com.clouddrive.gateway.filter;

import com.clouddrive.common.core.constant.SecurityConstants;
import com.clouddrive.common.core.constant.UrlStatus;
import com.clouddrive.common.core.util.JwtUtil;
import com.clouddrive.common.redis.util.RedisUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String url = request.getURI().getPath();
        if (!matches(url)) {
            return chain.filter(exchange);
        }

        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!cookies.containsKey("Token")) {
            return gotoUrl(response, UrlStatus.LOGIN_URL);
        }
        String tokenString = cookies.getFirst("Token").getValue();
        if (!StringUtils.hasLength(tokenString)) {
            return gotoUrl(response, UrlStatus.LOGIN_URL);
        }
        try {
            String uuid = JwtUtil.getUUID(tokenString);
            if (redisUtil.getString(uuid) == null) {
                log.info("过期token");
                return gotoUrl(response, UrlStatus.LOGIN_URL);
            }
        } catch (JwtException e) {
            log.info("非法token");
            return gotoUrl(response, UrlStatus.LOGIN_URL);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }


    Mono<Void> gotoUrl(ServerHttpResponse response, String url) {
        response.setStatusCode(HttpStatus.OK);
        HttpHeaders headers = response.getHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
//        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = "{" +
                "\"message\":\"鉴权失败\"," +
                "\"data\":\"" + url + "\"," +
                "\"status\": 303" +
                "}";
        DataBuffer buffer = response.bufferFactory().allocateBuffer().write(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private void addHeader(ServerHttpRequest.Builder mutate, String name, Object value) {
        if (value == null) {
            return;
        }
        String valueStr = value.toString();
        String valueEncode = "";
        try {
            valueEncode = URLEncoder.encode(valueStr, "utf-8");
        } catch (Exception e) {
        }
        mutate.header(name, valueEncode);
    }

    boolean matches(String path) {
        return !path.matches("(/api/auth/login|/api/auth/register)") && path.matches("/api/[\\s\\S]*");
    }
}
