package com.homework.musinsa.application.port.out;

import com.homework.musinsa.domain.PointPolicy;

import java.util.Optional;

public interface PointPolicyPort {
    Optional<PointPolicy> findActivePolicy();
}
