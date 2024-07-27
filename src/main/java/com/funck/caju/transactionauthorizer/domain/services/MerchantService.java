package com.funck.caju.transactionauthorizer.domain.services;

import java.util.Optional;

public interface MerchantService {

    Optional<String> getMccByMerchantName(final String merchantName);

}
