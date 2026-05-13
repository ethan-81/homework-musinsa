package com.homework.musinsa.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "point_policy")
public class PointPolicyEntity {
    // === 식별자 ===
    @Id
    @Column(updatable = false, nullable = false)
    private final Long id;

    // === Mutable ===
    @Column(updatable = true, nullable = false)
    private Long maxChargePoint;

    @Column(updatable = true, nullable = false)
    private Long maxHoldPoint;

    @Column(updatable = true, nullable = false)
    private int validPeriodInDays;
}
