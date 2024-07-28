package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.model.Balance;
import com.funck.caju.transactionauthorizer.domain.model.BalanceType;
import com.funck.caju.transactionauthorizer.domain.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class BalanceServiceImplTest {

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    private Balance balance;

    @BeforeEach
    void setUp() {
        final var account = Account.builder()
                .id(1L)
                .totalBalance(BigInteger.valueOf(1000))
                .build();

        balance = Balance.builder()
                .id(1L)
                .totalBalance(BigInteger.valueOf(1000))
                .account(account)
                .balanceType(BalanceType.CASH)
                .build();

        account.setBalances(List.of(balance));

        openMocks(this);
    }

    @Test
    @DisplayName("Should save balance and return saved balance")
    void testSaveBalance() {
        // a
        doReturn(balance).when(balanceRepository).save(any(Balance.class));

        // a
        final var savedBalance = balanceService.save(balance);

        // a
        assertNotNull(savedBalance);

        assertEquals(balance.getId(), savedBalance.getId());
        assertEquals(balance.getTotalBalance(), savedBalance.getTotalBalance());
        assertEquals(balance.getAccount(), savedBalance.getAccount());
        assertEquals(balance.getBalanceType(), savedBalance.getBalanceType());

        verify(balanceRepository, times(1)).save(balance);
    }

}