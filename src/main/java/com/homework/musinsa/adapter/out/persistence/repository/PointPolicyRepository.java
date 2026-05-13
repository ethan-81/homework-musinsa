package com.homework.musinsa.adapter.out.persistence.repository;

import com.homework.musinsa.adapter.out.persistence.entity.PointPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicyEntity, Long> {

    @Query("SELECT p " +
            "FROM PointPolicyEntity p " +
            "ORDER BY p.id DESC " +
            "LIMIT 1 ")
    Optional<PointPolicyEntity> findActivePolicy();
}
