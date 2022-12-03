package com.clouddrive.common.feign.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

//    @Bean("RequestInterceptor")
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//            if (requestAttributes != null) {
//                HttpServletRequest request = requestAttributes.getRequest();
//                if (request != null) {
//                    Cookie[] cookies = request.getCookies();
//                }
//            }
//        };
//    }
}
