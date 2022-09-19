package com.clouddrive.login.controller;

import com.clouddrive.login.util.RedisUtil;
import com.clouddrive.model.UserMode;
import com.clouddrive.login.service.impl.UserServiceImpl;
import com.clouddrive.login.util.BaseController;
import com.clouddrive.login.util.ReturnMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@ResponseBody
@Controller
@RequestMapping("api")
public class LoginController extends BaseController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("login")
    ReturnMode<Object> Login(HttpServletResponse response, @RequestParam String Name, @RequestParam String Pwds) throws IOException {
        UserMode user = new UserMode();
        user.setName(Name);
        user.setPwd(Pwds);
        //检查登录数据合法性
        if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getPwd())) {
            response.sendRedirect("/login?meg=null");
            return Error("用户名或密码为空！");
        }
        UserMode userMode = (UserMode) userService.loadUserByUsername(user.getUsername());
        if (userMode == null) {
            response.sendRedirect("/login?meg=usernull");
            return Error("用户不存在！");
        }
        if (!userMode.getPassword().equals(user.getPassword())) {
            response.sendRedirect("/login?meg=uperror");
            return Error("用户名或密码错误！");
        }
        //登录数据合法性得到验证，派发Token
        String hashStr = DigestUtils.md5DigestAsHex((userMode.getName() + ":" + new Date()).getBytes());
        redisUtil.addAndSetTimeOut(hashStr, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userMode));
        Cookie cookie = new Cookie("Token", hashStr);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.sendRedirect("/");
        return OK(hashStr);
    }
}
