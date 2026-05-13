package com.homework.musinsa.adapter.out.persistence;

import com.homework.musinsa.adapter.out.persistence.entity.PointDepositEntity;
import com.homework.musinsa.adapter.out.persistence.mapper.PointDepositMapper;
import com.homework.musinsa.adapter.out.persistence.repository.PointDepositRepository;
import com.homework.musinsa.adapter.out.persistence.util.JpaOptimisticLockHandler;
import com.homework.musinsa.application.port.out.PointDepositPort;
import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.common.pagination.PointDepositCursor;
import com.homework.musinsa.common.util.ListUtils;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.code.PointType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointDepositAdapter implements PointDepositPort {
    // DB나 JDBC 드라이버의 패킷/파라미터 크기 제한을 고려한 안전한 크기
    private static final int WRITE_CHUNK_SIZE = 1000;

    private final PointDepositRepository pointDepositRepository;
    private final JpaOptimisticLockHandler jpaOptimisticLockHandler;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<PointDeposit> findDepositBy(long depositId) {
        try {
            return pointDepositRepository.findById(depositId)
                    .map(PointDepositMapper::toDomain);
        } catch (RuntimeException exception) {
            String message =
                    String.format("포인트 적립금 조회(findDepositBy) 중 DB 오류 발생. 적립금 ID : '%s'", depositId);
            log.error(message, exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CursorPager.Result<PointDeposit, PointDepositCursor> findDepositForDeduct(
            long accountId,
            PointType pointType,
            LocalDate processedDate,
            CursorPager.Request<PointDepositCursor> request) {
        try {
            List<PointDepositEntity> pointDepositEntities =
                    pointDepositRepository.findExpiringSoonActiveDepositsWithCursorForUpdate(
                            accountId,
                            pointType,
                            processedDate,
                            request.cursor().expireDate(),
                            request.cursor().depositId(),
                            PageRequest.of(0, request.pageSize() + 1));

            if (pointDepositEntities.isEmpty()) {
                return CursorPager.Result.empty();
            }

            boolean hasNext = pointDepositEntities.size() > request.pageSize();
            List<PointDepositEntity> resultEntity =
                    hasNext ? pointDepositEntities.subList(0, request.pageSize()) : pointDepositEntities;
            List<PointDeposit> result = resultEntity.stream().map(PointDepositMapper::toDomain).toList();


            return CursorPager.Result.of(result, hasNext, PointDepositCursor.from(result.getLast()));
        } catch (RuntimeException exception) {
            log.error("포인트 적립금 목록 조회(findDeductibleDepositsWithCursor) 중 DB 오류 발생.", exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<PointDeposit> findDepositsByIds(List<Long> depositIds) {
        try {
            return pointDepositRepository.findDepositsByIdsForUpdate(depositIds).stream()
                    .map(PointDepositMapper::toDomain)
                    .toList();
        } catch (RuntimeException exception) {
            log.error("포인트 적립금 목록 조회(findDepositsByIds) 중 DB 오류 발생.", exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void savePointDeposit(PointDeposit pointDeposit) {
        try {
            jpaOptimisticLockHandler.persistCreateOrUpdate(
                    pointDeposit,
                    PointDeposit::getId,
                    PointDepositEntity.class,
                    pointDepositRepository,
                    PointDepositMapper::applyBalanceToEntityFromDomain,
                    PointDepositMapper::toEntity);
        } catch (OptimisticLockingFailureException exception) {
            // ToDo: 낙관적 락 발생 시 재시도 정책을 정해야 함.
            log.error("포인트 적립금 생성 또는 변경(createOrUpdateDeposits) 처리 중 버전 충돌 발생", exception);
            throw exception;
        } catch (RuntimeException exception) {
            log.error("포인트 적립금 생성 또는 변경(createOrUpdateDeposits) 처리 중 DB 오류 발생.", exception);
            throw exception;
        }
    }


    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void bulkBalanceUpdate(List<PointDeposit> deposits) {
        if (deposits == null || deposits.isEmpty()) {
            return;
        }

        try {
            ListUtils.partitionList(deposits, WRITE_CHUNK_SIZE)
                    .forEach(
                            subList -> {
                                final LocalDateTime updatedAt = LocalDateTime.now();
                                List<PointDepositEntity> depositEntities =
                                        subList.stream().map(PointDepositMapper::toEntity).toList();
                                pointDepositRepository.bulkBalanceUpdate(depositEntities, updatedAt);
                            });
        } catch (RuntimeException exception) {
            log.error("포인트 적립금 대량 변경(bulkBalanceUpdate) 처리 중 DB 오류 발생.", exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void bulkCreate(List<PointDeposit> deposits) {
        if (deposits == null || deposits.isEmpty()) {
            return;
        }

        try {
            ListUtils.partitionList(deposits, WRITE_CHUNK_SIZE)
                    .forEach(
                            subList -> {
                                final LocalDateTime createAt = LocalDateTime.now();
                                List<PointDepositEntity> depositEntities =
                                        subList.stream().map(PointDepositMapper::toEntity).toList();
                                pointDepositRepository.bulkInsert(depositEntities, createAt);
                            });
        } catch (RuntimeException exception) {
            log.error("포인트 적립금 대량 생성(bulkCreate) 처리 중 DB 오류 발생.", exception);
            throw exception;
        }
    }
}
