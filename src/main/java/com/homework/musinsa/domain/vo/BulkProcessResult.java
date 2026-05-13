package com.homework.musinsa.domain.vo;

public record BulkProcessResult(
        Point amountToProcess,
        Point processedAmount,
        long processedCount) {

    public BulkProcessResult {
        if (amountToProcess == null) {
            throw new IllegalArgumentException("amountToProcess must not be null");
        }
        if (processedAmount == null) {
            throw new IllegalArgumentException("processedAmount must not be null");
        }

        if (processedCount < 0) {
            throw new IllegalArgumentException("totalProcessedCount must be non-negative");
        }

        if (processedAmount.isGreaterThan(amountToProcess)) {
            throw new IllegalArgumentException("processedAmount must not exceed amountToProcess");
        }
    }

    public static BulkProcessResult of(
            Point amountToProcess,
            Point processedAmount,
            long processedCount) {
        return new BulkProcessResult(amountToProcess, processedAmount, processedCount);
    }

    public Point remainingAmount() {
        return amountToProcess.subtract(processedAmount);
    }
}
