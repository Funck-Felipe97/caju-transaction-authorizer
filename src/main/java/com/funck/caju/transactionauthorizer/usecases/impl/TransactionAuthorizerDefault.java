package com.funck.caju.transactionauthorizer.usecases.impl;

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

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class TransactionAuthorizerDefault implements TransactionAuthorizerUseCase {

    private final MerchantService merchantService;
    private final AccountService accountService;
    private final BalanceService balanceService;

    @Override
    public TransactionResponse execute(final TransactionRequest transactionRequest) {
        final String mcc = getMcc(transactionRequest);
        final BalanceType balanceType = BalanceType.getBalanceTypeByMcc(mcc);
        final var account = accountService.getAccountById(transactionRequest.account());

        if (processTransaction(account, balanceType, transactionRequest.totalAmount())) {
            return new TransactionResponse(TransactionResponseType.APPROVED);
        }

        return new TransactionResponse(TransactionResponseType.REJECTED);
    }

    private String getMcc(TransactionRequest transactionRequest) {
        return merchantService
                .getMccByMerchantName(transactionRequest.merchant())
                .orElse(transactionRequest.mcc());
    }

    private boolean processTransaction(Account account, BalanceType balanceType, BigInteger transactionTotalAmount) {
        final var balanceToBeDebited = account.getBalanceByType(balanceType);

        if (balanceToBeDebited.hasEnoughBalance(transactionTotalAmount)) {
            processApprovedTransaction(account, balanceToBeDebited, transactionTotalAmount);
            return true;
        }

        if (!BalanceType.CASH.equals(balanceType) && account.hasEnoughBalanceByTypeWithCash(transactionTotalAmount, balanceType)) {
            processApprovedTransactionWithCash(account, balanceToBeDebited, transactionTotalAmount);
            return true;
        }

        return false;
    }

    private void processApprovedTransaction(Account account, Balance balanceToBeDebited, BigInteger transactionTotalAmount) {
        account.subtractBalanceFrom(transactionTotalAmount, balanceToBeDebited);
        saveAccountAndBalances(account, balanceToBeDebited);
    }

    private void processApprovedTransactionWithCash(Account account, Balance balanceToBeDebited, BigInteger transactionTotalAmount) {
        final var cashBalance = account.getBalanceByType(BalanceType.CASH);
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