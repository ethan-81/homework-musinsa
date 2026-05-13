package com.homework.musinsa.adapter.out.persistence;


import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEntity;
import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionEventEntity;
import com.homework.musinsa.adapter.out.persistence.mapper.PointTransactionMapper;
import com.homework.musinsa.adapter.out.persistence.repository.PointTransactionEventRepository;
import com.homework.musinsa.adapter.out.persistence.repository.PointTransactionRepository;
import com.homework.musinsa.adapter.out.persistence.util.JpaOptimisticLockHandler;
import com.homework.musinsa.application.port.out.PointTransactionPort;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointTransactionAdapter implements PointTransactionPort {
    private final PointTransactionRepository pointTransactionRepository;
    private final PointTransactionEventRepository pointTransactionEventRepository;
    private final JpaOptimisticLockHandler jpaOptimisticLockHandler;

    @Override
    public Optional<PointTransaction> findDuplicateTransaction(String userId, String idempotencyKey) {
        try {
            return pointTransactionRepository
                    .findByIdempotencyKey(userId, idempotencyKey)
                    .map(transactionEntity -> {
                        List<PointTransactionEventEntity> eventEntities =
                                pointTransactionEventRepository.findByTransactionId(transactionEntity.getId());
                        return PointTransactionMapper.toTransactionDomain(transactionEntity, eventEntities);
                    });
        } catch (RuntimeException exception) {
            String message =
                    String.format("중복 포인트 거래 조회 중 DB 오류 발생. 사용자 ID: '%s', 거래 Key : '%s' ", userId, idempotencyKey);
            log.error(message, exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<PointTransaction> findByTransactionId(String userId, String transactionId) {
        try {
            return pointTransactionRepository
                    .findByCanonicalIdAndUserId(transactionId, userId)
                    .map(transactionEntity -> {
                        List<PointTransactionEventEntity> eventEntities =
                                pointTransactionEventRepository.findByTransactionId(transactionEntity.getId());
                        return PointTransactionMapper.toTransactionDomain(transactionEntity, eventEntities);
                    });
        } catch (RuntimeException exception) {
            String message =
                    String.format("포인트 거래 조회 중 DB 오류 발생. 사용자 ID: '%s', 거래 ID : '%s' ", userId, transactionId);
            log.error(message, exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveTransaction(PointTransaction transaction, PointTransactionEvent event) {
        try {
            jpaOptimisticLockHandler.persistCreateOrUpdate(
                    transaction,
                    PointTransaction::getId,
                    PointTransactionEntity.class,
                    pointTransactionRepository,
                    PointTransactionMapper::applyStatusToEntityFromDomain,
                    PointTransactionMapper::newTransactionEntityFrom);

            pointTransactionEventRepository.save(PointTransactionMapper.newEventEntityFrom(event));
        } catch (OptimisticLockingFailureException exception) {
            // ToDo: 낙관적 락 발생 시 재시도 정책을 정해야 함.
            String message =
                    String.format("거래 저장 중 버전 충돌 발생. 거래 ID : '%s', 이벤트 ID : '%s'", transaction.getId(), event.getId());
            log.error(message, exception);
        } catch (RuntimeException exception) {
            String message =
                    String.format("거래 저장 중 DB 오류 발생. 거래 ID : '%s', 이벤트 ID : '%s'", transaction.getId(), event.getId());
            log.error(message, exception);
            throw exception;
        }
    }
}

