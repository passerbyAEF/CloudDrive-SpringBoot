package com.clouddrive.common.core.exception;

public class TransactionalException extends SQLException {
    public TransactionalException() {
        super();
    }

    public TransactionalException(String message) {
        super(message);
    }
}
