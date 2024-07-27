package com.funck.caju.transactionauthorizer.usecases.model;


import com.funck.caju.transactionauthorizer.domain.model.Transaction;

public record TransactionResult(TransactionResponseType transactionResponseType, Transaction transaction) {

    public TransactionResult(TransactionResponseType transactionResponseType) {
        this(transactionResponseType, null);
    }

}
