package com.funck.caju.transactionauthorizer.usecases.impl;


import com.funck.caju.transactionauthorizer.domain.model.*;
import com.funck.caju.transactionauthorizer.domain.services.AccountService;
import com.funck.caju.transactionauthorizer.domain.services.BalanceService;
import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import com.funck.caju.transactionauthorizer.domain.services.TransactionService;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResponseType;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TransactionAuthorizerDefaultTest {

    @InjectMocks
    private TransactionAuthorizerDefault transactionAuthorizerDefault;

    @Mock
    private MerchantService merchantService;

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private TransactionService transactionService;

    private ValidateTransactionCommand validateTransactionCommand;
    private Account account;
    private Balance cashBalance;
    private Balance mealBalance;
    private Balance foodBalance;

    @BeforeEach
    void setUp() {
        openMocks(this);

        cashBalance = Balance.builder()
                .balanceType(BalanceType.CASH)
                .id(1234L)
                .totalBalance(BigDecimal.valueOf(300))
                .build();

        mealBalance = Balance.builder()
                .balanceType(BalanceType.MEAL)
                .id(1235L)
                .totalBalance(BigDecimal.valueOf(200))
                .build();

        foodBalance = Balance.builder()
                .balanceType(BalanceType.FOOD)
                .id(1236L)
                .totalBalance(BigDecimal.valueOf(500))
                .build();

        account = Account.builder()
                .id("1234")
                .totalBalance(BigDecimal.valueOf(1000))
                .balances(List.of(cashBalance, foodBalance, mealBalance))
                .build();

        foodBalance.setAccount(account);
        mealBalance.setAccount(account);
        cashBalance.setAccount(account);

        doReturn(account).when(accountService).getAccountById("1234");
        doReturn(Optional.of("5411")).when(merchantService).getMccByMerchantName("PADARIA DO ZE               SAO PAULO BR");
    }

    @Test
    @DisplayName("Reject transaction when account balance has not balance for mcc category")
    void testRejectTransactionWithNotEnoughBalance() {
        /*
         * CASH        = 0
         * FOOD        = 0
         * MEAL        = 200
         * MCC         = FOOD
         * TRANSACTION = 100
         */

        // a
        account.setBalances(List.of(mealBalance));

        validateTransactionCommand = new ValidateTransactionCommand(
                "1234", BigDecimal.valueOf(100), "5411", "PADARIA DO ZE               SAO PAULO BR"
        );

        // a
        final var transactionResult = transactionAuthorizerDefault.execute(validateTransactionCommand);

        // a
        assertEquals(TransactionResponseType.REJECTED, transactionResult.transactionResponseType());

        verify(accountService, times(1)).getAccountById("1234");
        verifyNoMoreInteractions(balanceService, accountService, transactionService);
    }

    @Test
    @DisplayName("Reject transaction when account balance is not enough for mcc category taking cash into account")
    void testRejectTransactionWithNotEnoughBalance2() {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = FOOD
         * TRANSACTION = 801
         */

        // a
        validateTransactionCommand = new ValidateTransactionCommand(
                "1234", BigDecimal.valueOf(801), "5411", "PADARIA DO ZE               SAO PAULO BR"
        );

        // a
        final var transactionResult = transactionAuthorizerDefault.execute(validateTransactionCommand);

        // a
        assertEquals(TransactionResponseType.REJECTED, transactionResult.transactionResponseType());

        verify(accountService, times(1)).getAccountById("1234");
        verifyNoMoreInteractions(balanceService, accountService, transactionService);
    }

    @Test
    @DisplayName("Approve transaction when category balance is enough for mcc category")
    void testApproveTransactionWhenCategoryBalanceIsEnough() {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = FOOD
         * TRANSACTION = 500
         */

        // a
        validateTransactionCommand = new ValidateTransactionCommand(
                "1234", BigDecimal.valueOf(500), "5411", "PADARIA DO ZE               SAO PAULO BR"
        );

        // a
        final var transactionResult = transactionAuthorizerDefault.execute(validateTransactionCommand);

        // a
        assertEquals(TransactionResponseType.APPROVED, transactionResult.transactionResponseType());

        verify(accountService, times(1)).save(account);
        verify(accountService, times(1)).getAccountById("1234");
        verify(balanceService, times(1)).save(foodBalance);
        verify(transactionService, times(1)).save(any(Transaction.class));
        verifyNoMoreInteractions(balanceService, accountService, transactionService);
    }

    @Test
    @DisplayName("Approve transaction when category balance plus cash balance is enough for mcc category")
    void testApproveTransactionWhenCategoryBalancePlusCashBalanceIsEnough() {
        /*
         * CASH        = 300
         * FOOD        = 500
         * MEAL        = 200
         * MCC         = FOOD
         * TRANSACTION = 799
         */

        // a
        validateTransactionCommand = new ValidateTransactionCommand(
                "1234", BigDecimal.valueOf(799), "5411", "PADARIA DO ZE               SAO PAULO BR"
        );

        // a
        final var transactionResult = transactionAuthorizerDefault.execute(validateTransactionCommand);

        // a
        assertEquals(TransactionResponseType.APPROVED, transactionResult.transactionResponseType());

        verify(accountService, times(1)).save(account);
        verify(accountService, times(1)).getAccountById("1234");
        verify(balanceService, times(1)).save(cashBalance);
        verify(balanceService, times(1)).save(foodBalance);
        verify(transactionService, times(1)).save(any(Transaction.class));
        verifyNoMoreInteractions(balanceService, accountService, transactionService);
    }

    @Test
    @DisplayName("Should use balance for merchant name saved on database when present")
    void testUseBalanceCategoryForMerchantName() {
        /*
         * MCC INPUT                = MEAL
         * MCC MERCHANT NAME        = FOOD
         */

        // a
        validateTransactionCommand = new ValidateTransactionCommand(
                "1234", BigDecimal.valueOf(50), "5811", "MERCHANT FOOD CATEGORY"
        );

        doReturn(Optional.of("5412")).when(merchantService).getMccByMerchantName("MERCHANT FOOD CATEGORY");
        doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]).when(transactionService).save(any(Transaction.class));

        // a
        final var transactionResult = transactionAuthorizerDefault.execute(validateTransactionCommand);

        // a
        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        verify(transactionService, times(1)).save(captor.capture());
        verify(balanceService, times(1)).save(foodBalance);
        verifyNoMoreInteractions(balanceService);

        assertEquals("5412", transactionResult.transaction().getMcc());
        assertEquals(transactionResult.transaction(), captor.getValue());
    }

}