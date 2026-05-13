package com.homework.musinsa.adapter.out.persistence.entity;


import com.homework.musinsa.adapter.out.persistence.converter.TransactionStatusConverter;
import com.homework.musinsa.adapter.out.persistence.converter.TransactionTypeConverter;
import com.homework.musinsa.domain.code.TransactionStatus;
import com.homework.musinsa.domain.code.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
        name = "point_transaction",
        indexes = {
                @Index(name = "idx_pt_user_id", columnList = "user_id"),
        })
public class PointTransactionEntity {
    // === 식별자 ===
    @Id
    @Column(updatable = false, nullable = false)
    private final Long id;

    // === 외부 공개용 식별자 ===
    @Column(length = 20, updatable = false, nullable = false)
    private final String canonicalId;

    // === 감사 정보 (Auditing) ===
    @Embedded
    @Builder.Default private final Auditing.Mutable auditing = new Auditing.Mutable();

    // === 낙관적 락 (Optimistic Locking) ===
    @Version
    private Long version;

    // === 계좌 정보 (Immutable) ===
    @Column(length = 20, updatable = false, nullable = false)
    private final String userId;

    // === 거래 정보 (Immutable) ===
    @Convert(converter = TransactionTypeConverter.class)
    @Column(length = 50, updatable = false, nullable = false)
    private final TransactionType transactionType;

    @Column(updatable = false, nullable = false)
    private final Long amount;

    @Column(length = 50, updatable = false, nullable = false)
    private final String channelType;

    @Column(length = 20, updatable = false, nullable = false)
    private final String channelTransactionId;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime transactedAt;

    // === 거래 상태 (Mutable) ===
    @Convert(converter = TransactionStatusConverter.class)
    @Column(length = 50, updatable = true, nullable = false)
    private TransactionStatus status;

    public PointTransactionEntity updateStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }
}
