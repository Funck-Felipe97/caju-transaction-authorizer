package com.funck.caju.transactionauthorizer.controllers.request;

import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigInteger;

public record TransactionRequest(
        @NotNull Long account,
        @NotNull @Positive BigInteger totalAmount,
        @NotBlank String mcc,
        @NotBlank String merchant
) {

    public ValidateTransactionCommand toValidateTransactionCommand() {
        return new ValidateTransactionCommand(account, totalAmount, mcc, merchant);
    }

}


