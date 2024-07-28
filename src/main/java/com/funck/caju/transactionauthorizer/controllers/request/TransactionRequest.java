package com.funck.caju.transactionauthorizer.controllers.request;

import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull String account,
        @NotNull @Positive BigDecimal totalAmount,
        @NotBlank String mcc,
        @NotBlank String merchant
) {

    public ValidateTransactionCommand toValidateTransactionCommand() {
        return new ValidateTransactionCommand(account, totalAmount, mcc, merchant);
    }

}


