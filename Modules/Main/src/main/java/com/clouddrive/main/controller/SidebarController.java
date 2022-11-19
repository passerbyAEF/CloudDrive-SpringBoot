package com.clouddrive.main.controller;

import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ResponseBody
@RequestMapping("Sidebar")
public class SidebarController extends BaseController {

    @GetMapping("List")
    ReturnMode<Object> Login(HttpServletResponse response) throws IOException {
        return OK("[{title:'首页',img:'a'}]");
    }
}
