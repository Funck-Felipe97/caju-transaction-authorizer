package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.services.MerchantService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Override
    public Optional<String> getMccByMerchantName(final String merchantName) {
        return Optional.empty();
    }

}
