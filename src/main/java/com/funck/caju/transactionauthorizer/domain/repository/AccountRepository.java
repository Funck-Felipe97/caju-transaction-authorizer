package com.funck.caju.transactionauthorizer.domain.repository;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
