package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionDetailEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointTransactionDetailRepository
        extends JpaRepository<PointTransactionDetailEntity, Long>,
        PointTransactionDetailCustomRepository {

    Optional<PointTransactionDetailEntity> findByTransactionIdAndTransactionEventId(
            Long transactionId, Long transactionEventId);

    @Query(
            "SELECT p FROM PointTransactionDetailEntity p "
                    + "WHERE p.transactionId = :transactionId "
                    + "AND p.transactionEventId = :transactionEventId "
                    + "AND p.id > :lastIdCursor "
                    + "ORDER BY p.id ASC")
    List<PointTransactionDetailEntity> findDetailsWithCursor(
            @Param("transactionId") Long transactionId,
            @Param("transactionEventId") Long transactionEventId,
            @Param("lastIdCursor") Long lastIdCursor,
            Pageable pageable);
}
