package com.funck.caju.transactionauthorizer.usecases.model;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Transaction;

import java.math.BigDecimal;

public record ValidateTransactionCommand(String account, BigDecimal totalAmount, String mcc, String merchant) {

    public Transaction toTransactionDomain() {
        final var transaction = new Transaction();

        final var account = new Account();
        account.setId(this.account);

        transaction.setAccount(account);
        transaction.setMcc(mcc);
        transaction.setMerchant(merchant);
        transaction.setTotalAmount(totalAmount);

        return transaction;
    }

}


