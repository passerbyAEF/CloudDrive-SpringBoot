package com.clouddrive.auth.controller;

import com.clouddrive.auth.service.impl.UserServiceImpl;
import com.clouddrive.common.core.constant.UrlStatus;
import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
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
@RequestMapping("External")
public class LoginController extends BaseController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ObjectMapper objectMapper;

    //    @PreAuthorize("hasRole('ANONYMOUS')")
    @PostMapping("login")
    void Login(HttpServletResponse response, @RequestParam String email, @RequestParam String pwd, @RequestParam(defaultValue = "0") Boolean remember) throws IOException {
        //检查登录数据合法性
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pwd)) {
//            response.sendRedirect("/login?meg=null");
            GoToUrl(response, UrlStatus.LOGIN_URL);
            return;
        }
        UserMode userMode = userService.getUserByEmail(email);
        if (userMode == null) {
//            response.sendRedirect("/login?meg=usernull");
            GoToUrl(response, UrlStatus.LOGIN_URL);
            return;
        }
        pwd = DigestUtils.md5DigestAsHex((pwd).getBytes());
        if (!userMode.getPassword().equals(pwd)) {
//            response.sendRedirect("/login?meg=uperror");
            GoToUrl(response, UrlStatus.LOGIN_URL);
            return;
        }
        UserUtil.setToken(userMode, response, remember, redisUtil, objectMapper);
        GoToUrl(response, UrlStatus.ROOT_URL);
    }
}
