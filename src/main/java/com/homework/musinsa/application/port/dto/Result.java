package com.homework.musinsa.application.port.dto;

import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.vo.TransactionResult;

public record Result(
        String transactionId,
        Long processedAmount,
        String transactionType,
        String transactionEventType,
        boolean isAlreadyProcessed) {

    public Result {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction id must not be null or blank");
        }

        if (processedAmount == null) {
            throw new IllegalArgumentException("Processed amount must not be null");
        }

        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException("Transaction type must not be null or blank");
        }

        if (transactionEventType == null || transactionEventType.isBlank()) {
            throw new IllegalArgumentException("Transaction event type must not be null or blank");
        }
    }

    public static Result of(TransactionResult transactionResult) {
        PointTransaction transaction = transactionResult.transaction();
        PointTransactionEvent event = transactionResult.event();
        return new Result(
                transaction.getCanonicalId(),
                event.getAmount().value(),
                transaction.getTransactionType().getCode(),
                event.getEventType().getCode(),
                transactionResult.isAlreadyProcessed());
    }

    public static Result alreadyProcessed(PointTransaction transaction, PointTransactionEvent event) {

        return new Result(
                transaction.getCanonicalId(),
                event.getAmount().value(),
                transaction.getTransactionType().getCode(),
                event.getEventType().getCode(),
                true);
    }
}
