package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.exceptions.AccountNotFoundException;
import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.repository.AccountRepository;
import com.funck.caju.transactionauthorizer.domain.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(Integer id) {
        return accountRepository.findByIdWithBalances(id).orElseThrow(() -> new AccountNotFoundException(String.format("Account not found: %d", id)));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Account save(Account account) {
        return accountRepository.save(account);
    }

}
