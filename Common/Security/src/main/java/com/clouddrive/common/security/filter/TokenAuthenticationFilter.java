package com.clouddrive.common.security.filter;

import com.clouddrive.common.core.util.JwtUtil;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        System.out.println(request.getRequestURI());
        if (request.getCookies() != null)
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("Token"))
                    token = c.getValue();
            }
        if (token != null) {
            String uuid;
            //如果有Token
            try {
                uuid = JwtUtil.getUUID(token);
            } catch (JwtException e) {
                SecurityContextHolder.getContext().setAuthentication(getAnonymousAuthentication());
                filterChain.doFilter(request, response);
                return;
            }

            String str = redisUtil.getString(uuid);
            if (str != null) {
                //如果Redis中存放着Token，就提取出缓存在Redis中的用户信息
                UserMode user = objectMapper.readValue(str, UserMode.class);
                //将用户实体存放进Security上下文中
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            SecurityContextHolder.getContext().setAuthentication(getAnonymousAuthentication());
        }

        filterChain.doFilter(request, response);
    }

    AnonymousAuthenticationToken getAnonymousAuthentication() {
        return new AnonymousAuthenticationToken("Anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }
}
