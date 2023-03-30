package com.clouddrive.auth.controller;

import com.clouddrive.auth.service.UserService;
import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ResponseBody
@Controller
@RequestMapping("External")
public class UserController extends BaseController {

    @Autowired
    UserService userService;


    @GetMapping("isAdmin")
    ReturnMode<Object> LoginOut(HttpServletResponse response, HttpServletRequest request) throws IOException {
        UserMode user = UserUtil.getUser();
        return OK(userService.isAdmin(user));
    }
}
