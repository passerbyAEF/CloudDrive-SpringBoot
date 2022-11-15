package com.clouddrive.common.security.util;

import com.clouddrive.common.security.domain.UserMode;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    public static UserMode getUser() {
        return (UserMode) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
