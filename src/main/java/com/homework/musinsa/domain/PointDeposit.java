package com.homework.musinsa.domain;

import com.homework.musinsa.common.util.IdGenerator;
import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.domain.vo.Point;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
public class PointDeposit {
    private final long id;
    private final long accountId;
    private final PointType pointType;
    private final Point depositAmount;
    private final Point balance;
    private final Point expiredAmount;
    private final LocalDate expiresDate;
    private final boolean isExpired;

    public static PointDeposit create(
            long accountId,
            PointType pointType,
            Point depositAmount,
            LocalDate processDate,
            int daysToExpire) {

        IdGenerator.GeneratedId generatedId = IdGenerator.generate();
        LocalDate expiresDate = processDate.plusDays(daysToExpire);

        return PointDeposit.builder()
                .id(generatedId.serial())
                .accountId(accountId)
                .pointType(pointType)
                .depositAmount(depositAmount)
                .balance(depositAmount)
                .expiredAmount(Point.zero())
                .expiresDate(expiresDate)
                .isExpired(false)
                .build();
    }

    public boolean isExpired(LocalDate transactionDate) {
        if (this.isExpired) {
            return true;
        }

        return this.expiresDate.isBefore(transactionDate);
    }

    public Point calculateAmountToDecrease(Point amountToDecrease) {
        if (this.isExpired) {
            return Point.zero();
        }

        if (amountToDecrease.isGreaterThan(this.balance)) {
            return this.balance;
        } else {
            return amountToDecrease;
        }
    }

    public PointDeposit increaseBalance(Point amount, LocalDate processDate) {
        this.ensureNotExpired(processDate);

        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("Amount must not be null or zero");
        }

        Point finalBalance = this.balance.add(amount);

        if (finalBalance.isGreaterThan(this.depositAmount)) {
            throw new IllegalStateException("Balance cannot exceed the deposit amount");
        }

        return this.toBuilder()
                .balance(finalBalance)
                .build();
    }

    public PointDeposit decreaseBalance(Point amount, LocalDate processDate) {
        this.ensureNotExpired(processDate);

        if (amount == null || amount.isZero()) {
            throw new IllegalArgumentException("Amount must not be null or zero");
        }

        Point finalBalance = this.balance.subtract(amount);

        if (finalBalance.isLessThan(Point.zero())) {
            throw new IllegalStateException("Balance cannot be negative");
        }

        return this.toBuilder().balance(finalBalance).build();
    }

    public PointDeposit expire(LocalDate processDate) {
        if (this.isExpired) {
            return this;
        }

        if (!this.expiresDate.isEqual(processDate)) {
            throw new IllegalStateException("Deposit is not expired");
        }

        return this.toBuilder()
                .balance(Point.zero())
                .expiredAmount(this.balance)
                .isExpired(true)
                .build();
    }

    private void ensureNotExpired(LocalDate processDate) {
        if (this.isExpired(processDate)) {
            throw new IllegalStateException("Deposit is expired");
        }
    }
}
