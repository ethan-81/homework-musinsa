package com.homework.musinsa.application.port.in;

import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.domain.code.UserType;

public interface ChargeUseCase {
    Result charge(ChargeCommand command);

    record ChargeCommand(
            String userId,
            long amount,
            String channelType,
            String channelTransactionId,
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason) {}
}
