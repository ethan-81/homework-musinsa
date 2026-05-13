package com.homework.musinsa.adapter.out.persistence.entity;


import com.homework.musinsa.adapter.out.persistence.converter.ProcessingCauseConverter;
import com.homework.musinsa.adapter.out.persistence.converter.ProcessingTypeConverter;
import com.homework.musinsa.domain.code.ProcessingCause;
import com.homework.musinsa.domain.code.ProcessingType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
        name = "point_transaction_detail",
        indexes = {@Index(name = "idx_ptd_event_id", columnList = "transaction_event_id")})
public class PointTransactionDetailEntity {
    // === 식별자 ===
    @Id
    @Column(updatable = false, nullable = false)
    private final Long id;

    // === 감사 정보 (Auditing) ===
    @Embedded
    @Builder.Default private final Auditing.Immutable audit = new Auditing.Immutable();

    // === 거래 정보 (Immutable) ===
    @Column(updatable = false, nullable = false)
    private final Long transactionId;

    @Column(updatable = false, nullable = false)
    private final Long transactionEventId;

    @Column(updatable = false, nullable = false)
    private final Long depositId;

    @Column(updatable = false, nullable = false)
    private final Long processedAmount;

    @Convert(converter = ProcessingCauseConverter.class)
    @Column(length = 50, nullable = false)
    private final ProcessingCause processingCause;

    @Convert(converter = ProcessingTypeConverter.class)
    @Column(length = 50, nullable = false)
    private final ProcessingType processingType;

    @Column(updatable = false, nullable = false)
    private final Long originalTransactionDetailId;
}
