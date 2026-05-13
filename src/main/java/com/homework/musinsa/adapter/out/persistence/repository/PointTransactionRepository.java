package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointTransactionRepository extends JpaRepository<PointTransactionEntity, Long> {
    @Query("SELECT p FROM PointTransactionEntity p " +
            "INNER JOIN PointTransactionEventEntity e ON e.transactionId = p.id " +
            "WHERE p.userId = :userId " +
            "AND e.idempotencyKey = :idempotencyKey")
    Optional<PointTransactionEntity> findByIdempotencyKey(
            @Param("userId") String userId,
            @Param("idempotencyKey") String idempotencyKey);

    Optional<PointTransactionEntity> findByCanonicalIdAndUserId(String canonicalId, String userId);
}
