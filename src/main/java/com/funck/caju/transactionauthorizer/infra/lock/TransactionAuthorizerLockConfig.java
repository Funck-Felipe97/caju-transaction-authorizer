/*
package com.funck.caju.transactionauthorizer.infra.lock;

import com.funck.caju.transactionauthorizer.usecases.TransactionAuthorizerUseCase;
import com.funck.caju.transactionauthorizer.usecases.impl.TransactionAuthorizerDefault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
@Profile("!dev")
public class TransactionAuthorizerLockConfig {

    @Bean
    @Primary
    public TransactionAuthorizerUseCase transactionAuthorizerLockProxy(final TransactionAuthorizerDefault transactionAuthorizer, final LockRegistry lockRegistry) {
        return new TransactionAuthorizerLockProxy(transactionAuthorizer, lockRegistry);
    }

}
*/
