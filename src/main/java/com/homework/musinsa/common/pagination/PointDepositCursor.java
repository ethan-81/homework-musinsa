package com.homework.musinsa.common.pagination;

import com.homework.musinsa.domain.PointDeposit;

import java.time.LocalDate;

public record PointDepositCursor(LocalDate expireDate, long depositId) implements Cursor {
    public PointDepositCursor {
        if (expireDate == null) {
            throw new IllegalArgumentException("expireDate must not be null");
        }

        if (depositId < 0) {
            throw new IllegalArgumentException("depositId must be greater than 0");
        }
    }

    public static PointDepositCursor from(PointDeposit deposit) {
        return new PointDepositCursor(deposit.getExpiresDate(), deposit.getId());
    }

    public static PointDepositCursor first() {
        return new PointDepositCursor(LocalDate.of(1990, 1, 1), 0L);
    }
}

