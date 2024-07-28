package com.funck.caju.transactionauthorizer.domain.model;

import com.funck.caju.transactionauthorizer.domain.exceptions.NotEnoughBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    private Account account;
    private Balance cashBalance;
    private Balance mealBalance;
    private Balance foodBalance;

    @BeforeEach
    void setUp() {
        cashBalance = Balance.builder()
                .balanceType(BalanceType.CASH)
                .id(1234L)
                .totalBalance(BigDecimal.valueOf(300))
                .build();

        mealBalance = Balance.builder()
                .balanceType(BalanceType.MEAL)
                .id(1235L)
                .totalBalance(BigDecimal.valueOf(200))
                .build();

        foodBalance = Balance.builder()
                .balanceType(BalanceType.FOOD)
                .id(1236L)
                .totalBalance(BigDecimal.valueOf(500))
                .build();

        account = Account.builder()
                .id("1234")
                .totalBalance(BigDecimal.valueOf(1000))
                .balances(List.of(cashBalance, foodBalance, mealBalance))
                .build();

        foodBalance.setAccount(account);
        mealBalance.setAccount(account);
        cashBalance.setAccount(account);
    }

    @Test
    @DisplayName("Should return account cash balance")
    void testGetCashBalance() {
        // a a
        final var cashBalance = account.getCashBalance();

        // a
        assertTrue(cashBalance.isPresent());
        assertEquals(this.cashBalance, cashBalance.get());
    }

    @Test
    @DisplayName("Should return account balance by type")
    void testGetBalanceByType() {
        // a a
        final var cashBalance = account.getBalanceByType(BalanceType.CASH);
        final var mealBalance = account.getBalanceByType(BalanceType.MEAL);
        final var foodBalance = account.getBalanceByType(BalanceType.FOOD);

        // a
        assertTrue(cashBalance.isPresent());
        assertTrue(mealBalance.isPresent());
        assertTrue(foodBalance.isPresent());

        assertEquals(this.cashBalance, cashBalance.get());
        assertEquals(this.mealBalance, mealBalance.get());
        assertEquals(this.foodBalance, foodBalance.get());
    }

    @Test
    @DisplayName("Should subtract balance from balance category")
    void testSubtractBalanceFromBalanceCategory() {
        // a a
        account.subtractBalanceFrom(new BigDecimal("70"), mealBalance);

        // a
        assertEquals(new BigDecimal("130"), mealBalance.getTotalBalance());
        assertEquals(new BigDecimal("300"), cashBalance.getTotalBalance());
        assertEquals(new BigDecimal("500"), foodBalance.getTotalBalance());
        assertEquals(new BigDecimal("930"), account.getTotalBalance());
    }

    @Test
    @DisplayName("Should subtract balance from balance category and cash when category balance is not enough")
    void testSubtractBalanceFromBalanceCategoryAndCash() {
        // a a
        account.subtractBalanceFrom(new BigDecimal("230"), mealBalance, cashBalance);

        // a
        assertEquals(new BigDecimal("0"), mealBalance.getTotalBalance());
        assertEquals(new BigDecimal("270"), cashBalance.getTotalBalance());
        assertEquals(new BigDecimal("500"), foodBalance.getTotalBalance());
        assertEquals(new BigDecimal("770"), account.getTotalBalance());
    }

    @Test
    @DisplayName("Should throw NotEnoughBalanceException when category balance is not enough")
    void testThrowExceptionWhenCategoryBalanceIsNotEnough() {
        // a a a
        final var notEnoughBalanceException = assertThrows(
                NotEnoughBalanceException.class,
                () -> account.subtractBalanceFrom(new BigDecimal("1100"), mealBalance)
        );

        assertEquals("Account balance not enough, totalAmount: 1100, totalBalance: 1000", notEnoughBalanceException.getMessage());
    }

    @Test
    @DisplayName("Should throw NotEnoughBalanceException when category balance and cash balance is not enough")
    void testThrowExceptionWhenCategoryBalanceAndCashIsNotEnough() {
        // a a a
        final var notEnoughBalanceException = assertThrows(
                NotEnoughBalanceException.class,
                () -> account.subtractBalanceFrom(new BigDecimal("501"), mealBalance, cashBalance)
        );

        assertEquals("Account balance not enough for this category, totalAmount: 301, totalBalance: 300", notEnoughBalanceException.getMessage());
    }

    @Test
    @DisplayName("should return true if account has enough balance for transaction amount")
    void testHasEnoughBalance() {
        assertTrue(account.hasEnoughBalance(new BigDecimal("300"), BalanceType.CASH));
        assertTrue(account.hasEnoughBalance(new BigDecimal("500"), BalanceType.MEAL));
        assertTrue(account.hasEnoughBalance(new BigDecimal("800"), BalanceType.FOOD));

        assertFalse(account.hasEnoughBalance(new BigDecimal("301"), BalanceType.CASH));
        assertFalse(account.hasEnoughBalance(new BigDecimal("501"), BalanceType.MEAL));
        assertFalse(account.hasEnoughBalance(new BigDecimal("801"), BalanceType.FOOD));
    }

    @Test
    @DisplayName("Should return category balance by merchant mcc")
    void testGetBalanceForMccCategory() {
        assertEquals(foodBalance, account.getBalanceForMccCategory("5411").get());
        assertEquals(foodBalance, account.getBalanceForMccCategory("5412").get());

        assertEquals(mealBalance, account.getBalanceForMccCategory("5811").get());
        assertEquals(mealBalance, account.getBalanceForMccCategory("5812").get());

        assertEquals(cashBalance, account.getBalanceForMccCategory("6011").get());
        assertEquals(cashBalance, account.getBalanceForMccCategory("6012").get());
    }

    @Test
    @DisplayName("Should return cash balance if mcc category does not have balance for category")
    void testGetBalanceForMccCategory2() {
        account.setBalances(List.of(mealBalance, cashBalance));

        assertEquals(cashBalance, account.getBalanceForMccCategory("5411").get());
    }

}