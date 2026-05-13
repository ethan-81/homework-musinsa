package com.homework.musinsa.adapter.out.persistence.entity;


import jakarta.persistence.Column;
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

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
        name = "point_account",
        indexes = {
                @Index(name = "idx_pa_user_id", columnList = "user_id"),
        })
public class PointAccountEntity {
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

    // === 사용자 정보 (Immutable) ===
    @Column(length = 20, updatable = false, nullable = false, unique = true)
    private final String userId;

    // === 잔액 정보 (Mutable) ===
    @Column(nullable = false)
    private Long balance;

    public PointAccountEntity updateBalance(long balance) {
        this.balance = balance;
        return this;
    }
}
