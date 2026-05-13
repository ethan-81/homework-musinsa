package com.homework.musinsa.adapter.in.dto;

import com.homework.musinsa.domain.code.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 취소 요청 모델")
public record ChargeCancelRequest(
        @Schema(description = "사용자 ID", maxLength = 20)
        String userId,
        @Schema(description = "원 거래 고유 번호", maxLength = 20)
        String transactionId,
        @Schema(description = "작업 요청 고유 Key", maxLength = 20)
        String idempotencyKey,
        @Schema(description = "요청자 유형")
        UserType requestUserType,
        @Schema(description = "요청자 ID", maxLength = 20)
        String requestUserId,
        @Schema(description = "요청 사유", maxLength = 200)
        String requestReason
) {
}
