package com.clouddrive.login.util;

import com.clouddrive.model.UserMode;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    protected ReturnMode<Object> Error(String message){
        return new ReturnMode<>(message, "Error");
    }
    protected ReturnMode<Object> OK(Object data){
        return new ReturnMode<>(data, "OK");
    }

    protected UserMode getUser(){
        return (UserMode) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
