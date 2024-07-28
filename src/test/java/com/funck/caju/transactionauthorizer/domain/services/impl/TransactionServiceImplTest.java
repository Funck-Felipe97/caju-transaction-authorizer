package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Transaction;
import com.funck.caju.transactionauthorizer.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        final var account = Account.builder()
                .id("1")
                .totalBalance(BigDecimal.valueOf(1000))
                .build();

        transaction = Transaction.builder()
                .id(1)
                .totalAmount(BigDecimal.valueOf(500))
                .mcc("5411")
                .merchant("PADARIA DO ZE               SAO PAULO BR")
                .account(account)
                .build();

        openMocks(this);
    }

    @Test
    @DisplayName("Should save transaction and return saved transaction")
    void testSaveTransaction() {
        // a
        doReturn(transaction).when(transactionRepository).save(any(Transaction.class));

        // a
        final var savedTransaction = transactionService.save(transaction);

        // a
        assertNotNull(savedTransaction);

        assertEquals(transaction.getId(), savedTransaction.getId());
        assertEquals(transaction.getTotalAmount(), savedTransaction.getTotalAmount());
        assertEquals(transaction.getMcc(), savedTransaction.getMcc());
        assertEquals(transaction.getMerchant(), savedTransaction.getMerchant());
        assertEquals(transaction.getAccount(), savedTransaction.getAccount());

        verify(transactionRepository, times(1)).save(transaction);
    }
}
