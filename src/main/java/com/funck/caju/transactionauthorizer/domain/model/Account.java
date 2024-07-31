package com.funck.caju.transactionauthorizer.domain.model;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.funck.caju.transactionauthorizer.domain.model.BalanceType.CASH;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Account {

    @Id
    private String id;

    @NotNull
    @PositiveOrZero
    private BigDecimal totalBalance;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Balance> balances = new ArrayList<>();

    public boolean hasEnoughBalanceWihCash(final BigDecimal totalAmount, final BalanceType balanceType) {
        final var balanceByType = balances.stream()
                .filter(balance -> balance.getBalanceType().equals(balanceType) || CASH.equals(balance.getBalanceType()))
                .map(Balance::getTotalBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return balanceByType.compareTo(totalAmount) >= 0;
    }

    public Optional<Balance> getBalanceForMccCategory(final String mcc) {
        final var balanceType = BalanceType.getBalanceTypeByMcc(mcc);

        final var balanceForMccCategory = getBalanceByType(balanceType);

        if (balanceForMccCategory.isPresent() || CASH.equals(balanceType))
            return balanceForMccCategory;
        else
            return getCashBalance();
    }

    public Optional<Balance> getCashBalance() {
        return getBalanceByType(CASH);
    }

    public Optional<Balance> getBalanceByType(final BalanceType balanceType) {
        return balances.stream()
                .filter(balance -> balance.getBalanceType().equals(balanceType))
                .findFirst();
    }

    public void subtractBalanceFrom(final BigDecimal totalAmount, final Balance balance) {
        validateSufficientBalance(totalAmount);
        totalBalance = totalBalance.subtract(totalAmount);
        balance.subtract(totalAmount);
    }

    public void subtractBalanceFrom(final BigDecimal totalAmount, final Balance balance, final Balance cashBalance) {
        final var debitFromBalance = totalAmount.min(balance.getTotalBalance());
        final var debitFromCash = totalAmount.subtract(debitFromBalance);

        subtractBalanceFrom(debitFromBalance, balance);
        subtractBalanceFrom(debitFromCash, cashBalance);
    }

    private void validateSufficientBalance(BigDecimal totalAmount) {
        if (totalAmount.compareTo(totalBalance) > 0) {
            throw new NotEnoughBalanceException(String.format(
                    "Account balance not enough, totalAmount: %s, totalBalance: %s",
                    totalAmount, totalBalance));
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", totalBalance=" + totalBalance +
                ", balances=" + balances.size() +
                '}';
    }
}
