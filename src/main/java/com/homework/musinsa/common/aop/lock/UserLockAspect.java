package com.homework.musinsa.common.aop.lock;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class UserLockAspect {
    private final UserLevelLockManager lockManager;

    @Around("@annotation(userLock)")
    public Object lock(ProceedingJoinPoint joinPoint, UserLock userLock) throws Throwable {
        String userId = UserLockParser.getUserIdFromArgs(joinPoint, userLock.key());
        Object lock = lockManager.getLock(userId);

        synchronized (lock) {
            return joinPoint.proceed();
        }
    }
}
