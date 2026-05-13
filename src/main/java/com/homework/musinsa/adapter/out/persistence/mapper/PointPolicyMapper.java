package com.homework.musinsa.adapter.out.persistence.mapper;

import com.homework.musinsa.adapter.out.persistence.entity.PointPolicyEntity;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.vo.Point;

public class PointPolicyMapper {
    public static PointPolicy toDomain(PointPolicyEntity entity) {
        return PointPolicy.of(
                entity.getId(),
                Point.of(entity.getMaxChargePoint()),
                Point.of(entity.getMaxHoldPoint()),
                entity.getValidPeriodInDays());
    }
}
