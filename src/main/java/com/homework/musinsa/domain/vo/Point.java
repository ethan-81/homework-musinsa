package com.homework.musinsa.domain.vo;

public record Point(long value) {
    public Point {
        if (value < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public static Point of(long amount) {
        return new Point(amount);
    }

    public static Point zero() {
        return new Point(0);
    }

    public static Point copyOf(Point origin) {
        return new Point(origin.value);
    }

    public Point add(Point amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        return new Point(this.value + amount.value);
    }

    public Point subtract(Point amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        if (this.value < amount.value) {
            throw new IllegalArgumentException("Amount must be less than or equal to the current value");
        }

        return new Point(this.value - amount.value);
    }

    public boolean isZero() {
        return this.value == 0;
    }

    public boolean isEqual(Point amount) {
        return this.value == amount.value;
    }

    public boolean isGreaterThan(Point other) {
        return this.value > other.value;
    }

    public boolean isGreaterThanOrEqualTo(Point other) {
        return this.value >= other.value;
    }

    public boolean isLessThan(Point other) {
        return this.value < other.value;
    }

    public boolean isLessThanOrEqualTo(Point other) {
        return this.value <= other.value;
    }
}
