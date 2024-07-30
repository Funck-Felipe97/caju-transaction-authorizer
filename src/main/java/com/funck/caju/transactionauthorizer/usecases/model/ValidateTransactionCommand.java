package com.funck.caju.transactionauthorizer.usecases.model;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Transaction;

import java.math.BigDecimal;

public record ValidateTransactionCommand(String account, BigDecimal totalAmount, String mcc, String merchant) {

    public Transaction toTransactionDomain(final Account account, final String mcc) {
        return Transaction.builder()
                .account(account)
                .mcc(mcc)
                .merchant(merchant)
                .totalAmount(totalAmount)
                .build();
    }

}


