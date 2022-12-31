package com.clouddrive.common.core.exception;

public class TransactionalException extends RuntimeException {
    public TransactionalException() {
        super();
    }

    public TransactionalException(String message) {
        super(message);
    }
}
