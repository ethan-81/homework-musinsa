package com.homework.musinsa.domain;

import com.homework.musinsa.domain.code.UserType;

public record RequestInfo(
        String idempotencyKey,
        UserType requestUserType,
        String requestUserId,
        String requestReason) {

    public RequestInfo {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key must not be null or blank");
        }

        if (requestUserType == null) {
            throw new IllegalArgumentException("Request user type must not be null");
        }

        if (requestUserId == null || requestUserId.isBlank()) {
            throw new IllegalArgumentException("Request user id must not be null or blank");
        }

        if (requestReason == null || requestReason.isBlank()) {
            throw new IllegalArgumentException("Request reason must not be null or blank");
        }
    }

    public static RequestInfo of(
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason) {
        return new RequestInfo(idempotencyKey, requestUserType, requestUserId, requestReason);
    }

    public boolean isAdmin() {
        return UserType.ADMIN.equals(this.requestUserType);
    }
}
