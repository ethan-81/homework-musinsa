package com.homework.musinsa.adapter.out.persistence.mapper;


import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEntity;
import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEventEntity;
import com.homework.musinsa.domain.ChannelInfo;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.RequestInfo;
import com.homework.musinsa.domain.vo.Point;

import java.util.List;

public class PointTransactionMapper {

    public static PointTransactionEntity newTransactionEntityFrom(PointTransaction domain) {
        return PointTransactionEntity.builder()
                .id(domain.getId())
                .canonicalId(domain.getCanonicalId())
                .userId(domain.getUserId())
                .transactionType(domain.getTransactionType())
                .amount(domain.getAmount().value())
                .channelType(domain.getChannelInfo().channelType())
                .channelTransactionId(domain.getChannelInfo().channelTransactionId())
                .transactedAt(domain.getTransactedAt())
                .status(domain.getTransactionStatus())
                .build();
    }

    public static PointTransactionEntity applyStatusToEntityFromDomain(
            PointTransactionEntity entity, PointTransaction domain) {
        return entity.updateStatus(domain.getTransactionStatus());
    }

    public static PointTransaction toTransactionDomain(
            PointTransactionEntity entity, List<PointTransactionEventEntity> eventEntities) {

        ChannelInfo channelInfo = ChannelInfo.of(entity.getChannelType(), entity.getChannelTransactionId());

        List<PointTransactionEvent> events =
                eventEntities.stream().map(PointTransactionMapper::toEventDomain).toList();

        return PointTransaction.builder()
                .id(entity.getId())
                .canonicalId(entity.getCanonicalId())
                .userId(entity.getUserId())
                .transactionType(entity.getTransactionType())
                .amount(Point.of(entity.getAmount()))
                .channelInfo(channelInfo)
                .transactedAt(entity.getTransactedAt())
                .events(events)
                .build();
    }

    public static PointTransactionEventEntity newEventEntityFrom(PointTransactionEvent domain) {
        return PointTransactionEventEntity.builder()
                .id(domain.getId())
                .sequence(domain.getSequence())
                .transactionId(domain.getTransactionId())
                .transactionEventType(domain.getEventType())
                .amount(domain.getAmount().value())
                .idempotencyKey(domain.getRequestInfo().idempotencyKey())
                .requestUserType(domain.getRequestInfo().requestUserType())
                .requestUserId(domain.getRequestInfo().requestUserId())
                .requestReason(domain.getRequestInfo().requestReason())
                .processedAt(domain.getProcessedAt())
                .build();
    }

    private static PointTransactionEvent toEventDomain(PointTransactionEventEntity entity) {
        RequestInfo requestInfo =
                RequestInfo.of(
                        entity.getIdempotencyKey(),
                        entity.getRequestUserType(),
                        entity.getRequestUserId(),
                        entity.getRequestReason());

        return PointTransactionEvent.builder()
                .id(entity.getId())
                .sequence(entity.getSequence())
                .transactionId(entity.getTransactionId())
                .eventType(entity.getTransactionEventType())
                .amount(Point.of(entity.getAmount()))
                .requestInfo(requestInfo)
                .processedAt(entity.getProcessedAt())
                .build();
    }
}
