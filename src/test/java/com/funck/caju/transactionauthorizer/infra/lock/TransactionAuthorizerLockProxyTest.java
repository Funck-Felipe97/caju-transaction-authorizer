package com.funck.caju.transactionauthorizer.infra.lock;

import com.funck.caju.transactionauthorizer.usecases.impl.TransactionAuthorizerDefault;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.integration.support.locks.LockRegistry;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.openMocks;

class TransactionAuthorizerLockProxyTest {

    @InjectMocks
    private TransactionAuthorizerLockProxy transactionAuthorizerLockProxy;

    @Mock
    private TransactionAuthorizerDefault transactionAuthorizerUseCase;

    @Mock
    private LockRegistry lockRegistry;

    private ValidateTransactionCommand validateTransactionCommand;

    @BeforeEach
    void setUp() {
        openMocks(this);

        validateTransactionCommand = new ValidateTransactionCommand(
                1234L, BigInteger.valueOf(100), "5411", "PADARIA DO ZE               SAO PAULO BR"
        );
    }

    @Test
    @DisplayName("Should execute transaction when acquire account lock")
    @SneakyThrows
    void testExecuteTransactionWhenLockAcquired() {
        // a
        final var lockMock = mock(Lock.class);
        final var lockKey = validateTransactionCommand.account().toString();
        final var transactionResultMock = mock(TransactionResult.class);

        doReturn(lockMock).when(lockRegistry).obtain(lockKey);
        doReturn(true).when(lockMock).tryLock(100, TimeUnit.MILLISECONDS);
        doReturn(transactionResultMock).when(transactionAuthorizerUseCase).execute(validateTransactionCommand);

        // a
        final var transactionResul = transactionAuthorizerLockProxy.execute(validateTransactionCommand);

        // a
        assertEquals(transactionResultMock, transactionResul);

        verify(transactionAuthorizerUseCase, times(1)).execute(validateTransactionCommand);
        verify(lockMock).unlock();
        verify(lockRegistry).obtain(lockKey);
        verify(lockMock).tryLock(100, TimeUnit.MILLISECONDS);
        verifyNoMoreInteractions(transactionAuthorizerUseCase, lockRegistry, lockMock);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should not execute transaction when account lock is not available")
    void testNotExecuteTransactionWhenLockNotAcquired() {
        // a
        final var lockMock = mock(Lock.class);
        final var lockKey = validateTransactionCommand.account().toString();

        doReturn(lockMock).when(lockRegistry).obtain(lockKey);
        doReturn(false).when(lockMock).tryLock(100, TimeUnit.MILLISECONDS);

        // a
        final var lockException = Assertions.assertThrows(
                LockException.class,
                () -> transactionAuthorizerLockProxy.execute(validateTransactionCommand)
        );

        // a
        assertEquals("Unable to get lock for account: 1234", lockException.getMessage());
        verifyNoInteractions(transactionAuthorizerUseCase);
    }

}