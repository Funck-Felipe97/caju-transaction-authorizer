package com.funck.caju.transactionauthorizer.domain.repository;

import com.funck.caju.transactionauthorizer.domain.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Integer> {

}
