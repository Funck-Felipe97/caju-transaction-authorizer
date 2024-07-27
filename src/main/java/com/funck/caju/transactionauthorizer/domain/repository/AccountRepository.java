package com.funck.caju.transactionauthorizer.domain.repository;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.balances WHERE a.id = :id")
    Optional<Account> findByIdWithBalances(@Param("id") Integer id);

}
