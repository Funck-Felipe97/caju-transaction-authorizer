package com.funck.caju.transactionauthorizer.infra.lock;

import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;

import com.funck.caju.transactionauthorizer.usecases.impl.TransactionAuthorizerDefault;
import com.funck.caju.transactionauthorizer.usecases.model.TransactionResult;
import com.funck.caju.transactionauthorizer.usecases.model.ValidateTransactionCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Primary
@Profile("!dev")
public class TransactionAuthorizerLockProxy implements TransactionAuthorizerUseCase {

    private final TransactionAuthorizerUseCase transactionAuthorizerUseCase;
    private final LockRegistry lockRegistry;

    public TransactionAuthorizerLockProxy(final TransactionAuthorizerDefault transactionAuthorizer, final LockRegistry lockRegistry) {
        this.transactionAuthorizerUseCase = transactionAuthorizer;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public TransactionResult execute(final ValidateTransactionCommand validateTransactionCommand) {
        log.info("Getting account lock to authorize transaction {}", validateTransactionCommand.account());

        final var lock = lockRegistry.obtain(validateTransactionCommand.account().toString());

        try {
            final var lockAcquired = lock.tryLock(100, TimeUnit.MILLISECONDS);

            if (lockAcquired) {
                return transactionAuthorizerUseCase.execute(validateTransactionCommand);
            } else {
                log.error("Unable to get lock for account: {}", validateTransactionCommand.account());
                throw new LockException(String.format("Unable to get lock for account: %s", validateTransactionCommand.account()));
            }

        } catch (InterruptedException e) {
            log.error("Transaction timeout for account: {}", validateTransactionCommand.account());

            throw new LockException(String.format("Transaction timeout for account: %s", validateTransactionCommand.account()));
        } finally {
            log.debug("Unlocking account {}", validateTransactionCommand.account());

            lock.unlock();
        }
    }

}
