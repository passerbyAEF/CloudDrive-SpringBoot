package com.clouddrive.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clouddrive.login.mapper.UserMapper;
import com.clouddrive.login.service.UserService;
import com.clouddrive.model.data.UserMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserMode> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserMode getUserByEmail(String s) {
        return userMapper.selectOne(new QueryWrapper<UserMode>().eq("email", s));
    }

    @Override
    public Boolean register(UserMode user) {
        return userMapper.insert(user) == 1;
    }
}
