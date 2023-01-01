package com.clouddrive.common.core.exception;

public class AuthException extends RuntimeException {
    public AuthException() {
        super("权限错误！");
    }

    public AuthException(String message) {
        super(message);
    }
}
