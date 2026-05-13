package com.homework.musinsa.adapter.out.persistence.mapper;


import com.homework.musinsa.adapter.out.persistence.entity.PointAccountEntity;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.vo.Point;

public class PointAccountMapper {

    public static PointAccountEntity newEntityFrom(PointAccount domain) {
        return PointAccountEntity.builder()
                .id(domain.getId())
                .canonicalId(domain.getCanonicalId())
                .userId(domain.getUserId())
                .balance(domain.getBalance().value())
                .build();
    }

    public static PointAccountEntity applyBalanceToEntityFromDomain(
            PointAccountEntity entity, PointAccount domain) {
        return entity.updateBalance(domain.getBalance().value());
    }

    public static PointAccount toDomain(PointAccountEntity entity) {
        return PointAccount.builder()
                .id(entity.getId())
                .canonicalId(entity.getCanonicalId())
                .userId(entity.getUserId())
                .balance(Point.of(entity.getBalance()))
                .build();
    }
}
