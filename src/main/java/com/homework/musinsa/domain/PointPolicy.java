package com.homework.musinsa.domain;

import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.domain.vo.Point;

import java.util.List;

public record PointPolicy(
        long id,
        Point maxChargePoint,
        Point maxHoldPoint,
        int validPeriodInDays) {

    public PointPolicy {
        if (id < 0) {
            throw new IllegalArgumentException("id must be positive");
        }

        if (maxChargePoint == null) {
            throw new IllegalArgumentException("Max charge point cannot be null");
        }

        if (maxHoldPoint == null) {
            throw new IllegalArgumentException("Max hold point cannot be null");
        }

        if (validPeriodInDays < 0) {
            throw new IllegalArgumentException("Valid period in days must be positive");
        }
    }

    public static PointPolicy of(
            long id,
            Point maxChargePoint,
            Point maxHoldPoint,
            int validPeriodInDays) {
        return new PointPolicy(id, maxChargePoint, maxHoldPoint, validPeriodInDays);
    }

    public List<PointType> deductionOrderByPointType() {
        // ToDo : 비선형적인 데이터의 우선 순위를 정의하는 로직을 구현
        return List.of(PointType.ADMIN_POINT, PointType.FREE_POINT);
    }
}
