package com.clouddrive.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.clouddrive.auth.service.impl.UserServiceImpl;
import com.clouddrive.common.core.constant.UrlStatus;
import com.clouddrive.common.core.controller.BaseController;
import com.clouddrive.common.core.domain.ReturnMode;
import com.clouddrive.common.id.constant.WorkIDConstants;
import com.clouddrive.common.id.feign.GetIDFeign;
import com.clouddrive.common.mail.util.MailUtil;
import com.clouddrive.common.redis.util.RedisUtil;
import com.clouddrive.common.security.domain.UserMode;
import com.clouddrive.common.security.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@ResponseBody
@Controller
@RequestMapping("External")
public class RegisterController extends BaseController {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GetIDFeign getIDFeign;
    @Autowired
    MailUtil mailUtil;

    @PostMapping("register")
    ReturnMode<Object> Register(HttpServletResponse response, @RequestParam String name, @RequestParam String email, @RequestParam String pwd) throws IOException {
        //检查登录数据合法性
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(email) || StringUtils.isEmpty(pwd)) {
            return Error("用户名或密码为空！");
        }
        if (userService.getOne(new QueryWrapper<UserMode>().eq("email", email)) != null) {
            return Error("用户已被注册！");
        }

        UserMode user = new UserMode();
        user.setName(name);
        user.setEmail(email);
        //MD5
        pwd = DigestUtils.md5DigestAsHex((pwd).getBytes());
        user.setPwd(pwd);
        user.setCreateTime(new Date());
        user.setIsEnable(true);
        List<GrantedAuthority> li = new ArrayList<>();
        li.add(new SimpleGrantedAuthority("ROLE_USER"));
        user.setAuthorities(li);

        Long id = getIDFeign.getID(WorkIDConstants.RegisterID);
        if (id == -1) {
            return Error("注册服务问题！");
        }
        String idMd5 = DigestUtils.md5DigestAsHex((id.toString()).getBytes());
        redisUtil.addStringAndSetTimeOut("registerCode:" + idMd5, objectMapper.writeValueAsString(user), 1, TimeUnit.DAYS);
        try {
            mailUtil.sendMail(email, "CloudDriver验证", "http://localhost:8080/api/auth/mailCode/" + idMd5);

        } catch (MailException e) {
            log.error("发送邮件失败：" + e.getMessage());//记录错误日志)
            return Error("注册服务问题！");
        }

        return OK("注册成功");

    }

    @GetMapping("mailCode/{code}")
    void mailCode(HttpServletResponse response, @PathVariable("code") String code) throws IOException {
        String s = redisUtil.getString("registerCode:" + code);
        if (s != null) {
            UserMode user = objectMapper.readValue(s, UserMode.class);
            try{
                userService.register(user);
            }catch (RuntimeException e){
                set500(response);
                return;
            }
            UserUtil.setToken(user, response, false, redisUtil, objectMapper);
            GoToUrl(response, UrlStatus.ROOT_URL);
        } else
            set500(response);
    }
}
