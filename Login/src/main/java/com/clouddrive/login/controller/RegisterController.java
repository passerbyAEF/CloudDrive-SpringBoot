package com.clouddrive.login.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clouddrive.login.service.UserService;
import com.clouddrive.login.service.impl.UserServiceImpl;
import com.clouddrive.login.util.BaseController;
import com.clouddrive.login.util.RedisUtil;
import com.clouddrive.login.util.ReturnMode;
import com.clouddrive.model.UserMode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ResponseBody
@Controller
@RequestMapping("api")
public class RegisterController extends BaseController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("register")
    ReturnMode<Object> Register(HttpServletResponse response, @RequestParam String Name, @RequestParam String Email, @RequestParam String Pwd) throws IOException {
        //检查登录数据合法性
        if (StringUtils.isEmpty(Name) || StringUtils.isEmpty(Email) || StringUtils.isEmpty(Pwd)) {
//            response.sendRedirect("/login?meg=null");
            return Error("用户名或密码为空！");
        }
        if (userService.getOne(new QueryWrapper<UserMode>().eq("email", Email)) != null) {
//            response.sendRedirect("/login?meg=usernull");
            return Error("用户已被注册！");
        }

        UserMode user = new UserMode();
        user.setName(Name);
        user.setEmail(Email);

        //MD5
        Pwd = DigestUtils.md5DigestAsHex((Pwd).getBytes());

        user.setPwd(Pwd);
        user.setCreateTime(new Date());
        user.setIsEnable(true);
        List<GrantedAuthority> li = new ArrayList<>();
        li.add(new SimpleGrantedAuthority("ROLE_USER"));
        user.setAuthorities(li);

        if(!userService.register(user)) return Error("500");

        setToken(user, response);
        response.sendRedirect("/");
        return OK("注册成功");
    }
}
