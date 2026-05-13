package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointTransactionEventRepository
        extends JpaRepository<PointTransactionEventEntity, Long> {
    List<PointTransactionEventEntity> findByTransactionId(Long transactionId);
}

