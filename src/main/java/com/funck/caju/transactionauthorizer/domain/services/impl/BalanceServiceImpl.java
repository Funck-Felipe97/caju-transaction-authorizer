package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.model.Balance;
import com.funck.caju.transactionauthorizer.domain.repository.BalanceRepository;
import com.funck.caju.transactionauthorizer.domain.services.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Balance save(final Balance balance) {
        log.info("Saving balance on database: {}", balance);

        return balanceRepository.save(balance);
    }

}
