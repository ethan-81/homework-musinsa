package com.homework.musinsa.adapter.out.persistence;


import com.homework.musinsa.adapter.out.persistence.entity.PointAccountEntity;
import com.homework.musinsa.adapter.out.persistence.mapper.PointAccountMapper;
import com.homework.musinsa.adapter.out.persistence.repository.PointAccountRepository;
import com.homework.musinsa.adapter.out.persistence.util.JpaOptimisticLockHandler;
import com.homework.musinsa.application.port.out.PointAccountPort;
import com.homework.musinsa.domain.PointAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointAccountAdapter implements PointAccountPort {
    private final PointAccountRepository pointAccountRepository;
    private final JpaOptimisticLockHandler jpaOptimisticLockHandler;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<PointAccount> findPointAccountBy(String userId) {
        try {
            return pointAccountRepository.findByUserId(userId).map(PointAccountMapper::toDomain);
        } catch (RuntimeException exception) {
            String message = String.format("사용자 포인트 계좌 조회 중 예기치 못한 DB 오류 발생. 사용자 ID: '%s'", userId);
            log.error(message, exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void savePointAccount(PointAccount pointAccount) {
        try {
            jpaOptimisticLockHandler.persistCreateOrUpdate(
                    pointAccount,
                    PointAccount::getId,
                    PointAccountEntity.class,
                    pointAccountRepository,
                    PointAccountMapper::applyBalanceToEntityFromDomain,
                    PointAccountMapper::newEntityFrom);
        } catch (OptimisticLockingFailureException exception) {
            // ToDo: 낙관적 락 발생 시 재시도 정책을 정해야 함.
            String message =
                    String.format("사용자 포인트 계좌 업데이트 중 버전 충돌 발생. 사용자 ID: '%s'", pointAccount.getUserId());
            log.error(message, exception);
            throw exception;
        } catch (RuntimeException exception) {
            String message = String.format("사용자 포인트 계좌 업데이트 중 예기치 못한 DB 오류 발생. 사용자 ID: '%s'", pointAccount.getUserId());
            log.error(message, exception);
            throw exception;
        }
    }
}
