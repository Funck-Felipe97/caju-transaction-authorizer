package com.funck.caju.transactionauthorizer.infra.lock;

public class LockException extends RuntimeException {

    public LockException(final String message) {
        super(message);
    }

}
