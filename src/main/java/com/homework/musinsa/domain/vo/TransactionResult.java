package com.homework.musinsa.domain.vo;

import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;

import java.time.LocalDate;

public record TransactionResult(
        PointTransaction transaction,
        PointTransactionEvent event,
        Point amountToProcess,
        LocalDate processDate,
        boolean isAlreadyProcessed) {

    public TransactionResult {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction must not be null");
        }

        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }

        if (amountToProcess == null) {
            throw new IllegalArgumentException("Amount to process must not be null");
        }

        if (processDate == null) {
            throw new IllegalArgumentException("Process date must not be null");
        }
    }

    public static TransactionResult of(
            PointTransaction transaction,
            PointTransactionEvent event) {

        return new TransactionResult(
                transaction,
                event,
                event.getAmount(),
                event.getProcessedAt().toLocalDate(),
                false);
    }

    public static TransactionResult alreadyProcessed(PointTransaction transaction, PointTransactionEvent event) {
        return new TransactionResult(
                transaction,
                event,
                event.getAmount(),
                event.getProcessedAt().toLocalDate(),
                true);
    }
}
