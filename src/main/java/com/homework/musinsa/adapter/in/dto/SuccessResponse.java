package com.homework.musinsa.adapter.in.dto;

import com.homework.musinsa.application.port.dto.Result;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 거래 응답 모델")
public record SuccessResponse(
        @Schema(description = "거래 고유 번호")
        String transactionId,
        @Schema(description = "처리 된 포인트 금액")
        Long processedAmount,
        @Schema(description = "요청한 거래 유형")
        String transactionType,
        @Schema(description = "요청한 거래 이벤트 유형")
        String transactionEventType,
        @Schema(description = "중복 처리 여부")
        boolean isAlreadyProcessed) {
    public SuccessResponse {
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction id must not be null or blank");
        }

        if (processedAmount == null) {
            throw new IllegalArgumentException("Processed amount must not be null");
        }

        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException("Transaction type must not be null or blank");
        }

        if (transactionEventType == null || transactionEventType.isBlank()) {
            throw new IllegalArgumentException("Transaction event type must not be null or blank");
        }
    }

    public static SuccessResponse from(Result result) {
        return new SuccessResponse(
                result.transactionId(),
                result.processedAmount(),
                result.transactionType(),
                result.transactionEventType(),
                result.isAlreadyProcessed());
    }
}
