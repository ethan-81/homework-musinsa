package com.homework.musinsa.common.pagination;

import com.homework.musinsa.domain.PointTransactionDetail;

public record PointTransactionDetailCursor(long id) implements Cursor {
    public PointTransactionDetailCursor {
        if (id < 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }

    public static PointTransactionDetailCursor from(PointTransactionDetail detail) {
        return new PointTransactionDetailCursor(detail.id());
    }

    public static PointTransactionDetailCursor first() {
        return new PointTransactionDetailCursor(0L);
    }
}
