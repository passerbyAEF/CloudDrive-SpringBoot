package com.clouddrive.common.core.controller;

import com.clouddrive.common.core.constant.HttpStatus;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BaseController {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    public ReturnMode<Object> Error() {
        return new ReturnMode<>(null, "Error");
    }

    public ReturnMode<Object> Error(String message) {
        return new ReturnMode<>(message, "Error");
    }

    public ReturnMode<Object> OK(Object data) {
        return new ReturnMode<>(data, "OK");
    }

    public ReturnMode<Object> OK() {
        return new ReturnMode<>(null, "OK");
    }

    public void GoToUrl(HttpServletResponse response, String url) throws IOException {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.sendRedirect("/");
    }

    public UserMode getUser() {
        return UserUtil.getUser();
    }

    public void setToken(UserMode user, HttpServletResponse response, boolean remember) throws IOException {
        String hashStr = DigestUtils.md5DigestAsHex((user.getEmail() + ":" + new Date()).getBytes());
        redisUtil.addStringAndSetTimeOut(hashStr, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user), 30, TimeUnit.DAYS);
        Cookie cookie = new Cookie("Token", hashStr);
        cookie.setPath("/");
        if (remember) cookie.setMaxAge(2592000);
        response.addCookie(cookie);
    }
}
