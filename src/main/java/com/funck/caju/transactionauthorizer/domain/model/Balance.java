package com.funck.caju.transactionauthorizer.domain.model;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigInteger;

@Data
@Entity
@Table(name = "account_balance")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "account_id_fk"))
    private Account account;

    @Enumerated(EnumType.STRING)
    private BalanceType balanceType;

    private BigInteger totalBalance;

    public void subtract(final BigInteger totalAmount) {
        if (!hasEnoughBalance(totalAmount)) {
            throw new NotEnoughBalanceException(
                    String.format("Account balance not enough for this category, totalAmount: %s, totalBalance: %s", totalAmount, totalBalance)
            );
        }

        totalBalance = totalBalance.subtract(totalAmount);
    }

    public boolean hasEnoughBalance(final BigInteger totalAmount) {
        return totalBalance.compareTo(totalAmount) >= 0;
    }

    @Override
    public String toString() {
        return "Balance{" +
                "id=" + id +
                ", account=" + account +
                ", balanceType=" + balanceType +
                ", totalBalance=" + totalBalance +
                '}';
    }
}
