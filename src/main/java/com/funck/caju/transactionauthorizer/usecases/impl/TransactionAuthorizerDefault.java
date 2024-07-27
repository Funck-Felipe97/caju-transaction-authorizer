package com.funck.caju.transactionauthorizer.usecases.impl;

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

@Service
@RequiredArgsConstructor
public class TransactionAuthorizerDefault implements TransactionAuthorizerUseCase {

    private final MerchantService merchantService;
    private final AccountService accountService;
    private final BalanceService balanceService;

    @Override
    public TransactionResponse execute(final TransactionRequest transactionRequest) {
        final var mcc = merchantService
                .getMccByMerchantName(transactionRequest.merchant())
                .orElse(transactionRequest.mcc());

        final var balanceType = BalanceType.getBalanceTypeByMcc(mcc);

        final var account = accountService.getAccountById(transactionRequest.account());

        final var balanceByTransactionType = account.getBalanceByType(balanceType);

        if (balanceByTransactionType.hasEnoughBalanceByType(transactionRequest.totalAmount())) {
            account.subtractBalanceFrom(transactionRequest.totalAmount(), balanceByTransactionType);

            accountService.save(account);
            balanceService.save(balanceByTransactionType);

            new TransactionResponse(TransactionResponseType.APPROVED);
        }

        if (BalanceType.CASH.equals(balanceType)) {
            return new TransactionResponse(TransactionResponseType.REJECTED);
        }

        if (account.hasEnoughBalanceByTypeWithCash(transactionRequest.totalAmount(), balanceType)) {
            final var cashBalance = account.getBalanceByType(BalanceType.CASH);

            account.subtractBalanceFrom(transactionRequest.totalAmount(), balanceByTransactionType, cashBalance);

            accountService.save(account);
            balanceService.save(balanceByTransactionType);
            balanceService.save(cashBalance);

            new TransactionResponse(TransactionResponseType.APPROVED);
        }

        return new TransactionResponse(TransactionResponseType.REJECTED);
    }

}