package com.funck.caju.transactionauthorizer.infra.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;

import javax.sql.DataSource;

@Configuration
@Profile("!dev")
public class JdbcLockConfig {

    @Bean
    DefaultLockRepository defaultLockRepository(final DataSource dataSource) {
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    JdbcLockRegistry jdbcLockRegistry(final LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }

}
