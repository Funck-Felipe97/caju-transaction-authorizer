package com.funck.caju.transactionauthorizer.domain.services.impl;

import com.funck.caju.transactionauthorizer.domain.model.Merchant;
import com.funck.caju.transactionauthorizer.domain.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class MerchantServiceImplTest {

    @InjectMocks
    private MerchantServiceImpl merchantService;

    @Mock
    private MerchantRepository merchantRepository;

    private Merchant merchant;

    @BeforeEach
    void setUp() {
        merchant = Merchant.builder()
                .id(1L)
                .mcc("5811")
                .name("PADARIA DO ZE               SAO PAULO BR")
                .build();

        openMocks(this);
    }

    @Test
    @DisplayName("Should return mcc from saved merchant")
    void testGetMccByMerchant() {
        // a
        doReturn(Optional.of(merchant)).when(merchantRepository).findByName("PADARIA DO ZE               SAO PAULO BR");

        // a
        final var mcc = merchantService.getMccByMerchantName("PADARIA DO ZE               SAO PAULO BR");

        // a
        assertTrue(mcc.isPresent());
        assertEquals("5811", mcc.get());

        verify(merchantRepository, times(1)).findByName("PADARIA DO ZE               SAO PAULO BR");
    }

    @Test
    @DisplayName("Should return empty mcc when merchant not found")
    void testGetMccByMerchantNameWhenMerchantNotFound() {
        // a
        doReturn(Optional.empty()).when(merchantRepository).findByName("PADARIA DO ZE               SAO PAULO BR");

        // a
        final var mcc = merchantService.getMccByMerchantName("PADARIA DO ZE               SAO PAULO BR");

        // a
        assertTrue(mcc.isEmpty());

        verify(merchantRepository, times(1)).findByName("PADARIA DO ZE               SAO PAULO BR");
    }
}
