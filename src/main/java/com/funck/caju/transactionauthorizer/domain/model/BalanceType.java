package com.funck.caju.transactionauthorizer.domain.model;

import java.util.Set;
import java.util.stream.Stream;

public enum BalanceType {
    FOOD("5411", "5412"), MEAL("5811", "5812"), CASH;

    private final Set<String> supportedMcc;

    BalanceType(String... supportedMcc) {
        this.supportedMcc = Set.of(supportedMcc);
    }

    public static BalanceType getBalanceTypeByMcc(String mcc) {
        return Stream.of(values())
                .filter(balanceType -> balanceType.supportedMcc.contains(mcc))
                .findFirst()
                .orElse(CASH);
    }

}
