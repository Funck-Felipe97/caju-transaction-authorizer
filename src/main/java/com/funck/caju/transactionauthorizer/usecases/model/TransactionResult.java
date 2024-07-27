package com.funck.caju.transactionauthorizer.usecases.model;


public record TransactionResponse(String code) {

    public TransactionResponse(TransactionResponseType transactionResponseType) {
        this(transactionResponseType.getCode());
    }

}
