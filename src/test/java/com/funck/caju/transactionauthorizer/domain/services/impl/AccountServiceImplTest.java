package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.exceptions.AccountNotFoundException;
import com.funck.caju.transactionauthorizer.domain.model.Account;
import com.funck.caju.transactionauthorizer.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .totalBalance(BigInteger.valueOf(1000))
                .build();

        openMocks(this);
    }

    @Test
    @DisplayName("Should found account by id")
    void testGetAccountById() {
        // a
        doReturn(Optional.of(account)).when(accountRepository).findByIdWithBalances(1L);

        // a
        final var savedAccount = accountService.getAccountById(1L);

        // a
        assertNotNull(savedAccount);
        assertEquals(account.getId(), savedAccount.getId());
        assertEquals(account.getTotalBalance(), savedAccount.getTotalBalance());
        verify(accountRepository, times(1)).findByIdWithBalances(1L);
    }

    @Test
    @DisplayName("Should return empty option when account not exists")
    void testGetAccountByIdWhenAccountNotExists() {
        // a
        doReturn(Optional.empty()).when(accountRepository).findByIdWithBalances(1L);

        // a
        final var exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(1L)
        );

        // a
        assertEquals("Account not found: 1", exception.getMessage());
        verify(accountRepository, times(1)).findByIdWithBalances(1L);
    }

    @Test
    @DisplayName("Should save account and return saved account")
    void testSaveAccount() {
        // a
        doReturn(account).when(accountRepository).save(any(Account.class));

        // a
        final var savedAccount = accountService.save(account);

        // a
        assertNotNull(savedAccount);
        assertEquals(account.getId(), savedAccount.getId());
        assertEquals(account.getTotalBalance(), savedAccount.getTotalBalance());
        verify(accountRepository, times(1)).save(account);
    }
}
