package com.funck.caju.transactionauthorizer.domain.services;

import com.funck.caju.transactionauthorizer.domain.model.Account;

public interface AccountService {

    Account getAccountById(Long id);

    Account save(Account account);

}
