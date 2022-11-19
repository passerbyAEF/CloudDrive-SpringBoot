package com.clouddrive.common.security.util;

import com.clouddrive.common.core.constant.SecurityConstants;
import com.clouddrive.common.core.util.JwtUtil;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserUtil {

    public static UserMode getUser() {
        return (UserMode) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static void setToken(UserMode user, HttpServletResponse response, boolean remember, RedisUtil redisUtil, ObjectMapper objectMapper) throws IOException {
//        String hashStr = DigestUtils.md5DigestAsHex((user.getEmail() + ":" + new Date()).getBytes());
        String uuid = UUID.randomUUID().toString();
        HashMap<String, String> body = new HashMap<>();
        body.put(SecurityConstants.USER_NAME, user.getName());
        body.put(SecurityConstants.USER_ID, user.getId().toString());
        body.put(SecurityConstants.USER_UUID, uuid);
        redisUtil.addStringAndSetTimeOut(uuid, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user), 30, TimeUnit.DAYS);

        String access_token = JwtUtil.createJwt(body);
        Cookie cookie = new Cookie("Token", access_token);
        cookie.setPath("/");
        if (remember) {
            cookie.setMaxAge(2592000);
        }
        response.addCookie(cookie);
    }
}
