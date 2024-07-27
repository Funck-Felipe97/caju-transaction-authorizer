package com.funck.caju.transactionauthorizer.usecases;

import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;

public interface TransactionAuthorizerUseCase {

    TransactionResult execute(ValidateTransactionCommand validateTransactionCommand);

}
