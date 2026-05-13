package com.homework.musinsa.adapter.out.persistence;

import com.homework.musinsa.adapter.out.persistence.mapper.PointPolicyMapper;
import com.homework.musinsa.adapter.out.persistence.repository.PointPolicyRepository;
import com.homework.musinsa.application.port.out.PointPolicyPort;
import com.homework.musinsa.domain.PointPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointPolicyAdapter implements PointPolicyPort {
    private final PointPolicyRepository pointPolicyRepository;

    @Override
    public Optional<PointPolicy> findActivePolicy() {
        try {
            return pointPolicyRepository.findActivePolicy()
                    .map(PointPolicyMapper::toDomain);
        } catch (RuntimeException exception) {
            log.error("포인트 정책 중 예기치 못한 DB 오류 발생.", exception);
            throw exception;
        }
    }
}
