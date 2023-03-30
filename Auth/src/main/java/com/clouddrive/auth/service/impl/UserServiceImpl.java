package com.clouddrive.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clouddrive.auth.mapper.FolderMapper;
import com.clouddrive.auth.mapper.UserMapper;
import com.clouddrive.auth.service.UserService;
import com.clouddrive.common.core.exception.TransactionalException;
import com.clouddrive.common.filecore.domain.FolderMode;
import com.clouddrive.common.security.domain.UserMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserMode> implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    FolderMapper folderMapper;



    @Override
    public UserMode getUserByEmail(String s) {
        return userMapper.selectOne(new QueryWrapper<UserMode>().eq("email", s));
    }


    @Override
    public Boolean isAdmin(UserMode user) {
        SimpleAuthorityMapper s=new SimpleAuthorityMapper();
        for (GrantedAuthority authority : user.getAuthorities()) {
            if(authority.getAuthority().equals("ROLE_ADMIN")){
               return true;
            }
        }
        return false;
    }

    @Transactional
    @Override
    public Boolean register(UserMode user) {
        if (userMapper.insert(user) != 1) {
            throw new TransactionalException("SQL执行错误");
        }
        FolderMode folder = new FolderMode();
        folder.setName(".");
        folder.setOwnerId(user.getId());
        folder.setParentId(null);
        Date now = new Date();
        folder.setCreateTime(now);
        folder.setUpdateTime(now);
        if (folderMapper.insert(folder) != 1) {
            throw new TransactionalException("SQL执行错误");
        }
        return true;
    }
}
