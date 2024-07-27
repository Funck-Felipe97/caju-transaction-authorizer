package com.funck.caju.transactionauthorizer.usecases;

import com.funck.caju.transactionauthorizer.usecases.model.TransactionRequest;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponse;

public interface TransactionAuthorizerUseCase {

    TransactionResponse execute(TransactionRequest transactionRequest);

}
