package com.homework.musinsa.domain;

import com.homework.musinsa.common.util.IdGenerator;
import com.homework.musinsa.domain.vo.Point;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PointAccount {
    private final long id;
    private final String canonicalId;
    private final String userId;
    private final Point balance;

    public static PointAccount create(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id must not be null or empty");
        }

        IdGenerator.GeneratedId generatedId = IdGenerator.generateWith("PA");

        return PointAccount.builder()
                .id(generatedId.serial())
                .canonicalId(generatedId.canonical())
                .userId(userId)
                .balance(Point.zero())
                .build();
    }

    public PointAccount increaseBalance(Point amount, Point maxHoldingAmount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount to increase must not be null");
        }

        if (amount.isZero()) {
            return this;
        }

        Point finalBalance = this.balance.add(amount);

        if (finalBalance.isGreaterThan(maxHoldingAmount)) {
            throw new IllegalStateException("Balance cannot exceed the maximum holding amount");
        }

        return this.toBuilder()
                .balance(finalBalance)
                .build();
    }

    public PointAccount decreaseBalance(Point amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount to decrease must not be null");
        }

        if (amount.isZero()) {
            return this;
        }

        Point finalBalance = this.balance.subtract(amount);

        if (finalBalance.isLessThan(Point.zero())) {
            throw new IllegalStateException("Balance cannot be negative");
        }

        return this.toBuilder()
                .balance(finalBalance)
                .build();
    }
}
