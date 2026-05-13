package com.homework.musinsa.common.aop.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserLevelLockManager {
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    public Object getLock(String userId) {
        return locks.computeIfAbsent(userId, k -> new Object());
    }
}
