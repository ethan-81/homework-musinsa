package com.homework.musinsa.adapter.out.persistence.mapper;


import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionDetailEntity;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.vo.Point;

public class PointTransactionDetailMapper {
    public static PointTransactionDetailEntity newEntityFrom(PointTransactionDetail domain) {
        return PointTransactionDetailEntity.builder()
                .id(domain.id())
                .transactionId(domain.transactionId())
                .transactionEventId(domain.transactionEventId())
                .depositId(domain.depositId())
                .processedAmount(domain.processedAmount().value())
                .processingCause(domain.processingCause())
                .processingType(domain.processingType())
                .originalTransactionDetailId(domain.originalTransactionDetailId())
                .build();
    }

    public static PointTransactionDetail toDomain(PointTransactionDetailEntity entity) {
        return new PointTransactionDetail(
                entity.getId(),
                entity.getTransactionId(),
                entity.getTransactionEventId(),
                entity.getDepositId(),
                Point.of(entity.getProcessedAmount()),
                entity.getProcessingCause(),
                entity.getProcessingType(),
                entity.getOriginalTransactionDetailId());
    }
}
