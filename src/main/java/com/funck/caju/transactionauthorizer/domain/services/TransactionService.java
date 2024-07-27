package com.funck.caju.transactionauthorizer.domain.services;

import com.funck.caju.transactionauthorizer.domain.model.Transaction;

public interface TransactionService {

    Transaction save(final Transaction transaction);

}
