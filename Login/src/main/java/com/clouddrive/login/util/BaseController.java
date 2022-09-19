package com.clouddrive.login.util;

import com.clouddrive.model.UserMode;
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

    protected UserMode getUser() {
        return (UserMode) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    protected void setToken(UserMode user, HttpServletResponse response) throws IOException {
        String hashStr = DigestUtils.md5DigestAsHex((user.getEmail() + ":" + new Date()).getBytes());
        redisUtil.addAndSetTimeOut(hashStr, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user));
        Cookie cookie = new Cookie("Token", hashStr);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
