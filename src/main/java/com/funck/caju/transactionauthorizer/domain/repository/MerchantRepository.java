package com.funck.caju.transactionauthorizer.domain.repository;

import com.funck.caju.transactionauthorizer.domain.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByName(final String name);

}
