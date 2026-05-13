package com.homework.musinsa.adapter.out.persistence.util;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * JPA의 낙관적 락(@Version)이 적용된 엔티티를 안전하게 생성 또는 수정하는 로직을 캡슐화합니다. '1차 캐시 기반 create or update' 패턴을 사용하여
 * Lost Update를 방지하고, UseCase 계층의 사전 조회 계약을 강제합니다.
 */
@Component
public class JpaOptimisticLockHandler {
    @PersistenceContext
    private EntityManager em;

    /**
     * 도메인 객체를 기반으로 영속성 컨텍스트(1차 캐시)의 상태에 따라 생성 또는 수정을 수행합니다. 1차 캐시에 엔티티가 존재하면 'update' 로직을, 존재하지 않으면
     * 'create' 로직을 실행합니다.
     */
    public <D, E, ID> E persistCreateOrUpdate(
            D domainObject,
            Function<D, ID> idExtractor,
            Class<E> entityClass,
            JpaRepository<E, ID> repository,
            BiFunction<E, D, E> updateAction,
            Function<D, E> createFunction) {

        return execute(
                domainObject,
                idExtractor,
                entityClass,
                (entityProxy) -> updateAction.apply(entityProxy, domainObject),
                () -> {
                    validateCreationPath(idExtractor.apply(domainObject), repository, entityClass);
                    return createFunction.apply(domainObject);
                });
    }

    /**
     * 도메인 객체를 기반으로 영속성 컨텍스트(1차 캐시)에 존재하는 엔티티를 '수정'합니다. 1차 캐시에 엔티티가 존재하지 않으면 IllegalStateException을
     * 발생시켜 사전 조회 계약을 강제합니다.
     */
    public <D, E, ID> E persistUpdate(
            D domainObject,
            Function<D, ID> idExtractor,
            Class<E> entityClass,
            BiFunction<E, D, E> updateAction) {

        final ID id = idExtractor.apply(domainObject);

        return execute(
                domainObject,
                idExtractor,
                entityClass,
                (entityProxy) -> updateAction.apply(entityProxy, domainObject),
                () -> {
                    // persistUpdate는 생성을 허용하지 않으므로, 캐시에 없으면 항상 예외를 던짐
                    throw new IllegalStateException(
                            "Entity is not in context: " + entityClass.getSimpleName() + "(id=" + id + ")");
                });
    }

    /**
     * 1차 캐시의 상태를 확인하고, 상태에 따라 적절한 액션(람다)을 실행하는 핵심 로직입니다.
     *
     * @param onLoaded 1차 캐시에 엔티티가 존재할 때 실행될 액션 (Function)
     * @param onEmpty 1차 캐시에 엔티티가 없을 때 실행될 액션 (Supplier)
     * @return 실행된 액션의 결과 엔티티
     */
    private <D, E, ID> E execute(
            D domainObject,
            Function<D, ID> idExtractor,
            Class<E> entityClass,
            Function<E, E> onLoaded,
            Supplier<E> onEmpty) {

        final ID id = idExtractor.apply(domainObject);
        final PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
        final E entityProxy = em.getReference(entityClass, id);

        if (unitUtil.isLoaded(entityProxy)) {
            // [수정 경로] 이미 영속성 컨텍스트에 있음
            // apply 내부에서 필드가 변경되면 트랜잭션 종료 시 Dirty Checking으로 자동 업데이트됨
            return onLoaded.apply(entityProxy);
        } else {
            // [생성 경로] 새 엔티티를 생성하고 영속화함
            E newEntity = onEmpty.get();
            em.persist(newEntity); // 명시적 생성
            return newEntity;
        }
    }

    /** 생성 경로의 사전 조건을 검증합니다. (가드 절) 1차 캐시에는 없지만 DB에는 존재하는, 아키텍처 계약 위반 상태를 감지합니다. */
    private <E, ID> void validateCreationPath(
            ID id, JpaRepository<E, ID> repository, Class<E> entityClass) {
        if (repository.existsById(id)) {
            throw new IllegalStateException(
                    "Architecture Violation: "
                            + entityClass.getSimpleName()
                            + "(id="
                            + id
                            + ") exists in DB but not in L1 Cache. Pre-loading is required for optimistic locking.");
        }
    }
}
