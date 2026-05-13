package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointDepositEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PointDepositCustomRepository {
    void bulkBalanceUpdate(List<PointDepositEntity> deposits, LocalDateTime updatedAt);

    void bulkInsert(List<PointDepositEntity> deposits, LocalDateTime createdAt);
}
