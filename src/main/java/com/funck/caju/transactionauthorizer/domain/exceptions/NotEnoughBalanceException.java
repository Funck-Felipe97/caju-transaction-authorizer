package com.funck.caju.transactionauthorizer.domain.exceptions;

public class NotEnoughBalanceException extends RuntimeException {

    public NotEnoughBalanceException(String message) {
        super(message);
    }

}
