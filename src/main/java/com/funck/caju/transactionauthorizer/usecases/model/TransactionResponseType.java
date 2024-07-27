package com.funck.caju.transactionauthorizer.usecases.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionResponseType {

    APPROVED("00"),
    REJECTED("51"),
    GENERIC_ERROR("07");

    private final String code;

}
