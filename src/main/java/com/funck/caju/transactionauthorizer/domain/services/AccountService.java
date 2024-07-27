package com.funck.caju.transactionauthorizer.domain.services;

import com.funck.caju.transactionauthorizer.domain.model.Account;

public interface AccountService {

    Account getAccountById(Integer id);

    Account save(Account account);

}
