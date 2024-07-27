package com.funck.caju.transactionauthorizer.controllers;

import com.funck.caju.transactionauthorizer.domain.repository.AccountRepository;
import com.funck.caju.transactionauthorizer.domain.repository.BalanceRepository;
import com.funck.caju.transactionauthorizer.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TestController {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping
    public String get() {
        accountRepository.findAll().forEach(System.out::println);
        balanceRepository.findAll().forEach(System.out::println);
        transactionRepository.findAll().forEach(System.out::println);

        return "Hello world";
    }

}
