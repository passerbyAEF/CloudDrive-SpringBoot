package com.clouddrive.main.util;

import com.clouddrive.model.data.UserMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class BaseController {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    protected ReturnMode<Object> Error(String message) {
        return new ReturnMode<>(message, "Error");
    }

    protected ReturnMode<Object> OK(Object data) {
        return new ReturnMode<>(data, "OK");
    }

    protected ReturnMode<Object> OK() {
        return new ReturnMode<>(null, "OK");
    }

    protected UserMode getUser() {
        return (UserMode) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected void setToken(UserMode user, HttpServletResponse response, boolean remember) throws IOException {
        String hashStr = DigestUtils.md5DigestAsHex((user.getEmail() + ":" + new Date()).getBytes());
        redisUtil.addAndSetTimeOut(hashStr, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user));
        Cookie cookie = new Cookie("Token", hashStr);
        cookie.setPath("/");
        if (remember) cookie.setMaxAge(2592000);
        response.addCookie(cookie);
    }
}
