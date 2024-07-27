package com.funck.caju.transactionauthorizer.domain.model;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigInteger;
import java.util.Set;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private BigInteger totalBalance;

    @OneToMany(mappedBy = "account")
    private Set<Balance> balances;

    public boolean hasEnoughBalanceByTypeWithCash(final BigInteger totalAmount, final BalanceType balanceType) {
        final var balanceByType = balances.stream()
                .filter(balance -> balance.getBalanceType().equals(balanceType))
                .map(Balance::getTotalBalance)
                .reduce(BigInteger.ZERO, BigInteger::add);

        return balanceByType.compareTo(totalAmount) >= 0;
    }

    public Balance getBalanceByType(final BalanceType balanceType) {
        return balances.stream()
                .filter(balance -> balance.getBalanceType().equals(balanceType))
                .findFirst()
                .orElse(null);
    }

    public void subtractBalanceFrom(final BigInteger totalAmount, final Balance balance) {
        if (totalAmount.compareTo(totalBalance) > 0) {
            throw new NotEnoughBalanceException(
                    String.format("Account balance not enough, totalAmount: %s, totalBalance: %s", totalAmount, totalBalance)
            );
        }

        totalBalance = totalBalance.subtract(totalAmount);

        balance.subtract(totalAmount);
    }

    public void subtractBalanceFrom(final BigInteger totalAmount, final Balance balance, final Balance cashBalance) {
        final BigInteger debitFromBalance;
        final BigInteger debitFromCash;

        if (totalAmount.compareTo(balance.getTotalBalance()) >= 0) {
            debitFromBalance = balance.getTotalBalance();
            debitFromCash = totalAmount.subtract(balance.getTotalBalance());
        } else {
            debitFromBalance = totalAmount;
            debitFromCash = BigInteger.ZERO;
        }

        subtractBalanceFrom(debitFromBalance, balance);
        subtractBalanceFrom(debitFromCash, cashBalance);
    }

}
