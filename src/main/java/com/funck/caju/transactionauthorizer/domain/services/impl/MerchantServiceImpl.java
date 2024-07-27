package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.model.Merchant;
import com.funck.caju.transactionauthorizer.domain.repository.MerchantRepository;
import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getMccByMerchantName(final String merchantName) {
        log.info("Finding merchant by merchant name: {}", merchantName);

        return merchantRepository
                .findByName(merchantName)
                .map(Merchant::getMcc);
    }

}
