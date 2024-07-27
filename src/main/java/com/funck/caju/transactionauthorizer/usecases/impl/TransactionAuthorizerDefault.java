package com.funck.caju.transactionauthorizer.usecases.impl;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Balance;
import com.funck.caju.transactionauthorizer.domain.model.BalanceType;
import com.funck.caju.transactionauthorizer.domain.services.AccountService;
import com.funck.caju.transactionauthorizer.domain.services.BalanceService;
import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionRequest;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponse;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class TransactionAuthorizerDefault implements TransactionAuthorizerUseCase {

    private final MerchantService merchantService;
    private final AccountService accountService;
    private final BalanceService balanceService;

    @Override
    @Transactional
    public TransactionResponse execute(final TransactionRequest transactionRequest) {
        final String mcc = getMcc(transactionRequest);
        final var account = accountService.getAccountById(transactionRequest.account());

        if (processTransaction(account, mcc, transactionRequest.totalAmount())) {
            return new TransactionResponse(TransactionResponseType.APPROVED);
        }

        return new TransactionResponse(TransactionResponseType.REJECTED);
    }

    private String getMcc(TransactionRequest transactionRequest) {
        return merchantService
                .getMccByMerchantName(transactionRequest.merchant())
                .orElse(transactionRequest.mcc());
    }

    private boolean processTransaction(Account account, String mcc, BigInteger transactionTotalAmount) {
        final var availableBalanceForMccCategoryOptional = account.getBalanceForMccCategory(mcc);

        if (availableBalanceForMccCategoryOptional.isEmpty()) {
            return false;
        }

        final var availableBalanceForMccCategory = availableBalanceForMccCategoryOptional.get();

        if (availableBalanceForMccCategory.hasEnoughBalance(transactionTotalAmount)) {
            processApprovedTransaction(account, availableBalanceForMccCategory, transactionTotalAmount);
            return true;
        }

        if (BalanceType.CASH.equals(availableBalanceForMccCategory.getBalanceType())) {
            return false;
        }

        if (account.hasEnoughBalanceByTypeWithCash(transactionTotalAmount, availableBalanceForMccCategory.getBalanceType())) {
            processApprovedTransactionWithCash(account, availableBalanceForMccCategory, transactionTotalAmount);
            return true;
        }

        return false;
    }

    private void processApprovedTransaction(Account account, Balance balanceToBeDebited, BigInteger transactionTotalAmount) {
        account.subtractBalanceFrom(transactionTotalAmount, balanceToBeDebited);
        saveAccountAndBalances(account, balanceToBeDebited);
    }

    private void processApprovedTransactionWithCash(Account account, Balance balanceToBeDebited, BigInteger transactionTotalAmount) {
        final var cashBalance = account.getCashBalance()
                .orElseThrow(() -> new NotEnoughBalanceException("Cash balance not found"));

        account.subtractBalanceFrom(transactionTotalAmount, balanceToBeDebited, cashBalance);
        saveAccountAndBalances(account, balanceToBeDebited, cashBalance);
    }

    private void saveAccountAndBalances(Account account, Balance... balances) {
        accountService.save(account);
        for (var balance : balances) {
            balanceService.save(balance);
        }
    }
}