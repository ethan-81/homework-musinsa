package com.homework.musinsa.adapter.out.persistence.entity;


import com.homework.musinsa.adapter.out.persistence.converter.PointTypeConverter;
import com.homework.musinsa.domain.code.PointType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "point_deposit")
public class PointDepositEntity {
    // === 식별자 ===
    @Id
    @Column(updatable = false, nullable = false)
    private final Long id;

    // === 감사 정보 (Auditing) ===
    @Embedded
    @Builder.Default private final Auditing.Mutable auditing = new Auditing.Mutable();

    // === 낙관적 락 (Optimistic Locking) ===
    @Version
    private Long version;

    // === 계좌 정보 (Immutable) ===
    @Column(updatable = false, nullable = false)
    private final Long accountId;

    @Convert(converter = PointTypeConverter.class)
    @Column(length = 50, updatable = false, nullable = false)
    private final PointType pointType;

    @Column(updatable = false, nullable = false)
    private final LocalDate expiresDate;

    @Column(updatable = false, nullable = false)
    private Long depositAmount;

    // === 잔액 정보 (Mutable) ===
    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private Long expiredAmount;

    @Column(nullable = false)
    private boolean isExpired;

    public PointDepositEntity updateBalance(long balance) {
        this.balance = balance;

        return this;
    }

    public PointDepositEntity updateToExpired(
            long balance,
            long expiredAmount,
            boolean isExpired) {
        this.balance = balance;
        this.expiredAmount = expiredAmount;
        this.isExpired = isExpired;

        return this;
    }
}
