package com.homework.musinsa.domain;

import com.homework.musinsa.common.util.IdGenerator;
import com.homework.musinsa.domain.code.TransactionEventType;
import com.homework.musinsa.domain.vo.Point;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointTransactionEvent {
    private final long id;
    private final int sequence;
    private final long transactionId;
    private final TransactionEventType eventType;
    private final Point amount;
    private final RequestInfo requestInfo;
    private final LocalDateTime processedAt;

    public static PointTransactionEvent createApproveEvent(
            long transactionId,
            Point amount,
            RequestInfo requestInfo,
            LocalDateTime processedAt) {

        return PointTransactionEvent.builder()
                .id(IdGenerator.generate().serial())
                .sequence(1)
                .transactionId(transactionId)
                .eventType(TransactionEventType.APPROVE)
                .amount(amount)
                .requestInfo(requestInfo)
                .processedAt(processedAt)
                .build();
    }

    public static PointTransactionEvent createCancelEvent(
            int sequence,
            long transactionId,
            Point amount,
            RequestInfo requestInfo,
            LocalDateTime processedAt) {

        return PointTransactionEvent.builder()
                .id(IdGenerator.generate().serial())
                .sequence(sequence)
                .transactionId(transactionId)
                .eventType(TransactionEventType.CANCEL)
                .amount(amount)
                .requestInfo(requestInfo)
                .processedAt(processedAt)
                .build();
    }

    public boolean isApproveEvent() {
        return TransactionEventType.APPROVE.equals(this.eventType);
    }

    public boolean isCanceledEvent() {
        return TransactionEventType.CANCEL.equals(this.eventType);
    }
}
