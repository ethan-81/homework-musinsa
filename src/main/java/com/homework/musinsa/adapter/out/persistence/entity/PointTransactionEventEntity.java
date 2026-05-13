package com.homework.musinsa.adapter.out.persistence.entity;


import com.homework.musinsa.adapter.out.persistence.converter.TransactionEventTypeConverter;
import com.homework.musinsa.adapter.out.persistence.converter.UserTypeConverter;
import com.homework.musinsa.domain.code.TransactionEventType;
import com.homework.musinsa.domain.code.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
        name = "point_transaction_event",
        indexes = {
                @Index(name = "idx_pte_transaction_id", columnList = "transaction_id"),
                @Index(name = "idx_pte_idempotency_key", columnList = "idempotency_key")})
public class PointTransactionEventEntity {
    // === 식별자 ===
    @Id
    @Column(updatable = false, nullable = false)
    private final Long id;

    // === 감사 정보 (Auditing) ===
    @Embedded
    @Builder.Default private final Auditing.Immutable audit = new Auditing.Immutable();

    // === 거래 정보 (Immutable) ===
    @Column(updatable = false, nullable = false)
    private final Integer sequence;

    @Column(updatable = false, nullable = false)
    private final Long transactionId;

    @Convert(converter = TransactionEventTypeConverter.class)
    @Column(length = 50, updatable = false, nullable = false)
    private final TransactionEventType transactionEventType;

    @Column(updatable = false, nullable = false)
    private final Long amount;

    @Column(length = 100, updatable = false, nullable = false)
    private final String idempotencyKey;

    @Convert(converter = UserTypeConverter.class)
    @Column(length = 50, updatable = false, nullable = false)
    private final UserType requestUserType;


    @Column(length = 20, updatable = false, nullable = false)
    private final String requestUserId;

    @Lob
    @Column(updatable = false, nullable = false)
    private final String requestReason;

    @Column(updatable = false, nullable = false)
    private final LocalDateTime processedAt;
}
