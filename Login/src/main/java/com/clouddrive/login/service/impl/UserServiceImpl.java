package com.clouddrive.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clouddrive.login.mapper.UserMapper;
import com.clouddrive.login.service.UserService;
import com.clouddrive.model.UserMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserMode> implements UserService, UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserMode user = userMapper.selectOne(new QueryWrapper<UserMode>().eq("email", s));
        return user;
    }

    @Override
    public Boolean register(UserMode user) {
        return userMapper.insert(user) == 1;
    }
}
