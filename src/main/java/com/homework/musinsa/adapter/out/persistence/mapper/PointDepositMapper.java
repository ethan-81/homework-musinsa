package com.homework.musinsa.adapter.out.persistence.mapper;


import com.homework.musinsa.adapter.out.persistence.entity.PointDepositEntity;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.vo.Point;

public class PointDepositMapper {

    public static PointDepositEntity toEntity(PointDeposit domain) {
        return PointDepositEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .pointType(domain.getPointType())
                .expiresDate(domain.getExpiresDate())
                .depositAmount(domain.getDepositAmount().value())
                .balance(domain.getBalance().value())
                .expiredAmount(domain.getExpiredAmount().value())
                .isExpired(domain.isExpired())
                .build();
    }

    public static PointDepositEntity applyBalanceToEntityFromDomain(
            PointDepositEntity entity, PointDeposit domain) {
        return entity.updateBalance(domain.getBalance().value());
    }

    public static PointDepositEntity applyExpiredToEntityFromDomain(
            PointDepositEntity entity, PointDeposit domain) {
        return entity.updateToExpired(
                domain.getBalance().value(),
                domain.getExpiredAmount().value(),
                domain.isExpired());
    }

    public static PointDeposit toDomain(PointDepositEntity entity) {
        return PointDeposit.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .pointType(entity.getPointType())
                .expiresDate(entity.getExpiresDate())
                .depositAmount(Point.of(entity.getDepositAmount()))
                .balance(Point.of(entity.getBalance()))
                .expiredAmount(Point.of(entity.getExpiredAmount()))
                .isExpired(entity.isExpired())
                .build();
    }
}
