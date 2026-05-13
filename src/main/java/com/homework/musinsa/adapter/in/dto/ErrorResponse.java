package com.homework.musinsa.adapter.in.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 응답 모델")
public record ErrorResponse(
        @Schema(description = "에러 코드") String errorCode,
        @Schema(description = "에러 메시지") String errorMessage,
        @Schema(description = "상세 에러 메시지") String errorDetailMessage) {}
