package com.funck.caju.transactionauthorizer.controllers.response;

import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;

public record TransactionResponse(String code) {

    public TransactionResponse(TransactionResult transactionResult) {
        this(transactionResult.transactionResponseType().getCode());
    }

}
