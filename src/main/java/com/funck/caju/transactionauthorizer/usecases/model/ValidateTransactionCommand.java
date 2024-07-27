package com.funck.caju.transactionauthorizer.usecases.model;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Transaction;

import java.math.BigInteger;

public record ValidateTransactionCommand(Long account, BigInteger totalAmount, String mcc, String merchant) {

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


