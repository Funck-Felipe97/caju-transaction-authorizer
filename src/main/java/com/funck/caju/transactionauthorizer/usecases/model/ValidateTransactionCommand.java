package com.funck.caju.transactionauthorizer.usecases.model;

import java.math.BigInteger;

public record TransactionRequest(Integer account, BigInteger totalAmount, String mcc, String merchant) {
}


