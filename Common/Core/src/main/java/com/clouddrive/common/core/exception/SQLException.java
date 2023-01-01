package com.clouddrive.common.core.exception;

public class SQLException extends RuntimeException{
    public SQLException() {
        super();
    }

    public SQLException(String message) {
        super(message);
    }
}
