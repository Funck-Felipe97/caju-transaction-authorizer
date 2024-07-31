package com.funck.caju.transactionauthorizer.usecases.impl;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Balance;
import com.funck.caju.transactionauthorizer.domain.services.AccountService;
import com.funck.caju.transactionauthorizer.domain.services.BalanceService;
import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import com.funck.caju.transactionauthorizer.domain.services.TransactionService;
import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
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
        log.info("Validating requested transaction: {}", validateTransactionCommand);

        final String mcc = getMcc(validateTransactionCommand);
        final var account = accountService.getAccountById(validateTransactionCommand.account());

        if (processTransaction(account, mcc, validateTransactionCommand.totalAmount())) {
            final var transaction = transactionService.save(validateTransactionCommand.toTransactionDomain(account, mcc));

            log.info("Requested transaction approved {}", transaction);

            return new TransactionResult(TransactionResponseType.APPROVED, transaction);
        }

        log.info("Requested transaction rejected...");

        return new TransactionResult(TransactionResponseType.REJECTED);
    }

    private String getMcc(ValidateTransactionCommand validateTransactionCommand) {
        return merchantService
                .getMccByMerchantName(validateTransactionCommand.merchant())
                .orElse(validateTransactionCommand.mcc());
    }

    private boolean processTransaction(Account account, String mcc, BigDecimal transactionTotalAmount) {
        final var availableBalanceForMccCategoryOptional = account.getBalanceForMccCategory(mcc);

        if (availableBalanceForMccCategoryOptional.isEmpty()) {
            return false;
        }

        final Balance availableBalanceForMccCategory = availableBalanceForMccCategoryOptional.get();

        // Try to debit from balance available for mcc type
        if (availableBalanceForMccCategory.hasEnoughBalance(transactionTotalAmount)) {
            account.subtractBalanceFrom(transactionTotalAmount, availableBalanceForMccCategory);
            saveAccountAndBalances(account, availableBalanceForMccCategory);
            return true;
        }

        // Try to debit from account available balance taking cash into account
        if (account.hasEnoughBalanceWihCash(transactionTotalAmount, availableBalanceForMccCategory.getBalanceType())) {
            final var cashBalance = account.getCashBalance().orElseThrow(() -> new NotEnoughBalanceException("Cash balance not found"));
            account.subtractBalanceFrom(transactionTotalAmount, availableBalanceForMccCategory, cashBalance);
            saveAccountAndBalances(account, availableBalanceForMccCategory, cashBalance);
            return true;
        }

        return false;
    }

    private void saveAccountAndBalances(Account account, Balance... balances) {
        accountService.save(account);

        for (var balance : balances) {
            balanceService.save(balance);
        }
    }
}