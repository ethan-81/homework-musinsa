package com.homework.musinsa.application.port.in;

import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.domain.code.UserType;

public interface UseCancelUseCase {
    Result cancel(CancelCommand command);

    record CancelCommand(
            String userId,
            String transactionId,
            long amount,
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason) {}
}
