package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointAccountRepository extends JpaRepository<PointAccountEntity, Long> {
    Optional<PointAccountEntity> findByUserId(String userId);
}
