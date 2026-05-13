package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionDetailEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface PointTransactionDetailCustomRepository {
    void bulkInsert(List<PointTransactionDetailEntity> details, LocalDateTime createdAt);
}
