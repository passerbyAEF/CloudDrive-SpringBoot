package com.clouddrive.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle start");
        Enumeration<String> headerNames = request.getHeaderNames();
        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("preHandle header中信息,headerName:{},headerValue:{}", headerName, request.getHeader(headerName));
        }
        log.info("preHandle requestURI:{}", request.getRequestURI());
        log.info("preHandle queryString:{}", request.getQueryString());

        Cookie[] cookies = request.getCookies();

        log.info("preHandle end");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) throws Exception {
        log.info("afterCompletion start");
        Enumeration<String> headerNames = request.getHeaderNames();
        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("afterCompletion header中信息,headerName:{},headerValue:{}", headerName, request.getHeader(headerName));
        }
        log.info("afterCompletion requestURI:{}", request.getRequestURI());
        log.info("afterCompletion queryString:{}", request.getQueryString());
        log.info("afterCompletion ex:{}" + ex);

        log.info("afterCompletion response响应状态码:{}", response.getStatus());
        log.info("afterCompletion end");
    }
}
