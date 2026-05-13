package com.homework.musinsa.adapter.in.dto;

import com.homework.musinsa.domain.code.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 요청 모델")
public record ChargeRequest(
        @Schema(description = "사용자 ID", maxLength = 20)
        String userId,
        @Schema(description = "충전 요청 금액")
        long amount,
        @Schema(description = "요청 채널", maxLength = 50)
        String channelType,
        @Schema(description = "요청 채널 고유 거래 번호", maxLength = 20)
        String channelTransactionId,
        @Schema(description = "작업 요청 고유 Key", maxLength = 20)
        String idempotencyKey,
        @Schema(description = "요청자 유형")
        UserType requestUserType,
        @Schema(description = "요청자 ID", maxLength = 20)
        String requestUserId,
        @Schema(description = "요청 사유", maxLength = 200)
        String requestReason) {
}
