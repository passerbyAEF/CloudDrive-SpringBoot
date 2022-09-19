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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    ReturnMode<Object> Login(HttpServletResponse response, @RequestParam String Email, @RequestParam String Pwd) throws IOException {
        //检查登录数据合法性
        if (StringUtils.isEmpty(Email) || StringUtils.isEmpty(Pwd)) {
//            response.sendRedirect("/login?meg=null");
            return Error("用户名或密码为空！");
        }
        UserMode userMode = (UserMode) userService.loadUserByUsername(Email);
        if (userMode == null) {
//            response.sendRedirect("/login?meg=usernull");
            return Error("用户不存在！");
        }
        Pwd = DigestUtils.md5DigestAsHex((Pwd).getBytes());
        if (!userMode.getPassword().equals(Pwd)) {
//            response.sendRedirect("/login?meg=uperror");
            return Error("用户名或密码错误！");
        }
        setToken(userMode, response);
        response.sendRedirect("/");
        return OK("登录成功");
    }
}
