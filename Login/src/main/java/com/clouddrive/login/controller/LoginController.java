package com.clouddrive.login.controller;

import com.clouddrive.login.util.RedisUtil;
import com.clouddrive.model.data.UserMode;
import com.clouddrive.login.service.impl.UserServiceImpl;
import com.clouddrive.login.util.BaseController;
import com.clouddrive.login.util.ReturnMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ResponseBody
@Controller
public class LoginController extends BaseController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("login")
    ReturnMode<Object> Login(HttpServletResponse response, @RequestParam String email, @RequestParam String pwd, @RequestParam(defaultValue = "0") Boolean remember) throws IOException {
        //检查登录数据合法性
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pwd)) {
//            response.sendRedirect("/login?meg=null");
            return Error("用户名或密码为空！");
        }
        UserMode userMode = userService.getUserByEmail(email);
        if (userMode == null) {
//            response.sendRedirect("/login?meg=usernull");
            return Error("用户不存在！");
        }
        pwd = DigestUtils.md5DigestAsHex((pwd).getBytes());
        if (!userMode.getPassword().equals(pwd)) {
//            response.sendRedirect("/login?meg=uperror");
            return Error("用户名或密码错误！");
        }
        setToken(userMode, response, remember);
        response.sendRedirect("/");
        return OK("登录成功");
    }
}
