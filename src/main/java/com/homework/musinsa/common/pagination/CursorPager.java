package com.homework.musinsa.common.pagination;

import java.util.List;

public final class CursorPager {
    private CursorPager() {}

    public static final int DEFAULT_BATCH_SIZE = 1000;

    public record Request<C extends Cursor>(C cursor, int pageSize) {
        public Request {
            if (cursor == null) {
                throw new IllegalArgumentException("Cursor must not be null");
            }

            if (pageSize <= 0) {
                throw new IllegalArgumentException("Page size must be greater than 0");
            }
        }

        public static <C extends Cursor> Request<C> first(C cursor) {
            return new Request<>(cursor,  DEFAULT_BATCH_SIZE);
        }

        public Request<C> nextFrom(CursorPager.Result<?, C> previousResult) {
            return new Request<>(previousResult.nextCursor(), this.pageSize);
        }
    }

    public record Result<T, C extends Cursor>(
            List<T> items,
            boolean hasNext,
            C nextCursor) {

        public static <T, C extends Cursor> Result<T, C> empty() {
            return new Result<>(List.of(), false, null);
        }

        public static <T, C extends Cursor> Result<T, C> of(List<T> items, boolean hasNext, C nextCursor) {
            if (items == null || items.isEmpty()) {
                return empty();
            }

            return new Result<>(items, hasNext, hasNext ? nextCursor : null);
        }

        public boolean hasItems() {
            return !this.items.isEmpty();
        }
    }
}

