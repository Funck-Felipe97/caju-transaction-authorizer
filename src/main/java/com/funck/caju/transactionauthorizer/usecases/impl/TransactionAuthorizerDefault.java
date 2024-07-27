package com.funck.caju.transactionauthorizer.usecases.impl;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Balance;
import com.funck.caju.transactionauthorizer.domain.model.BalanceType;
import com.funck.caju.transactionauthorizer.domain.services.AccountService;
import com.funck.caju.transactionauthorizer.domain.services.BalanceService;
import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import com.funck.caju.transactionauthorizer.domain.services.TransactionService;
import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;
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
    private final TransactionService transactionService;

    @Override
    @Transactional
    public TransactionResult execute(final ValidateTransactionCommand validateTransactionCommand) {
        final String mcc = getMcc(validateTransactionCommand);
        final var account = accountService.getAccountById(validateTransactionCommand.account());

        if (processTransaction(account, mcc, validateTransactionCommand.totalAmount())) {
            final var transaction = transactionService.save(validateTransactionCommand.toTransactionDomain());

            return new TransactionResult(TransactionResponseType.APPROVED, transaction);
        }

        return new TransactionResult(TransactionResponseType.REJECTED);
    }

    private String getMcc(ValidateTransactionCommand validateTransactionCommand) {
        return merchantService
                .getMccByMerchantName(validateTransactionCommand.merchant())
                .orElse(validateTransactionCommand.mcc());
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