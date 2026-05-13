package com.homework.musinsa.application.port.out;

import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;

import java.util.Optional;

public interface PointTransactionPort {
    Optional<PointTransaction> findDuplicateTransaction(String userId, String idempotencyKey);

    Optional<PointTransaction> findByTransactionId(String userId, String transactionId);

    void saveTransaction(PointTransaction transaction, PointTransactionEvent event);
}
