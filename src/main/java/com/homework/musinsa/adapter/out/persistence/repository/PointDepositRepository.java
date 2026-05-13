package com.homework.musinsa.adapter.out.persistence.repository;


import com.homework.musinsa.adapter.out.persistence.entity.PointDepositEntity;
import com.homework.musinsa.domain.code.PointType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PointDepositRepository
        extends JpaRepository<PointDepositEntity, Long>, PointDepositCustomRepository {
    Optional<PointDepositEntity> findById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            "SELECT p FROM PointDepositEntity p "
                    + "WHERE p.accountId = :accountId "
                    + "AND p.pointType = :pointType "
                    + "AND p.balance > 0 "
                    + "AND (p.expiresDate >= :processDate OR p.isExpired IS false )"
                    + "AND (p.expiresDate > :lastExpiresDateCursor OR (p.expiresDate = :lastExpiresDateCursor AND p.id > :lastIdCursor)) "
                    + "ORDER BY p.expiresDate ASC, p.id ASC")
    List<PointDepositEntity> findExpiringSoonActiveDepositsWithCursorForUpdate(
            @Param("accountId") Long accountId,
            @Param("pointType") PointType pointType,
            @Param("processDate") LocalDate processDate,
            @Param("lastExpiresDateCursor") LocalDate lastExpiresDateCursor,
            @Param("lastIdCursor") Long lastIdCursor,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PointDepositEntity p "
            + "WHERE p.id IN (:ids)")
    List<PointDepositEntity> findDepositsByIdsForUpdate(List<Long> ids);

}
