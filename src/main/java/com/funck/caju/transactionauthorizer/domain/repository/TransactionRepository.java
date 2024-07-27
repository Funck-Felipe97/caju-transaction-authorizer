package com.funck.caju.transactionauthorizer.domain.repository;

import com.funck.caju.transactionauthorizer.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}
