package com.clouddrive.auth.service;

import com.clouddrive.common.security.domain.UserMode;

public interface UserService {

    Boolean register(UserMode user);

    UserMode getUserByEmail(String s);
}
