package com.clouddrive.login.service;

import com.clouddrive.model.data.UserMode;

public interface UserService {

    Boolean register(UserMode user);

    UserMode getUserByEmail(String s);
}
