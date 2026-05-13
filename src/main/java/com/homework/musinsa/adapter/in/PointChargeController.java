package com.homework.musinsa.adapter.in;

import com.homework.musinsa.adapter.in.dto.ChargeCancelRequest;
import com.homework.musinsa.adapter.in.dto.ChargeRequest;
import com.homework.musinsa.adapter.in.dto.ErrorResponse;
import com.homework.musinsa.adapter.in.dto.SuccessResponse;
import com.homework.musinsa.application.port.in.ChargeCancelUseCase;
import com.homework.musinsa.application.port.in.ChargeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "포인트 충전", description = "포인트 충전 API")
public class PointChargeController {
    private final ChargeUseCase chargeUseCase;
    private final ChargeCancelUseCase chargeCancelUseCase;

    @PostMapping(path = "homework/point/charge", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "포인트 적립", description = "포인트를 적립하는 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "적립 완료"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 요청 정보",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(
                            responseCode = "500",
                            description = "알수 없는 오류 발생",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
            })
    ResponseEntity<SuccessResponse> chargePoint(
            @Valid @RequestBody final ChargeRequest request) {
        ChargeUseCase.ChargeCommand chargeCommand =
                new ChargeUseCase.ChargeCommand(
                        request.userId(),
                        request.amount(),
                        request.channelType(),
                        request.channelTransactionId(),
                        request.idempotencyKey(),
                        request.requestUserType(),
                        request.requestUserId(),
                        request.requestReason());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.from(chargeUseCase.charge(chargeCommand)));
    }

    @PostMapping(path = "homework/point/charge/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "포인트 적립 취소", description = "적립 된 포인트를 전액 취소하는 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "취소 완료"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 요청 정보",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(
                            responseCode = "500",
                            description = "알수 없는 오류 발생",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
            })
    ResponseEntity<SuccessResponse> chargeCancelPoint(
            @Valid @RequestBody final ChargeCancelRequest request) {
        ChargeCancelUseCase.CancelCommand cancelCommand =
                new ChargeCancelUseCase.CancelCommand(
                        request.userId(),
                        request.transactionId(),
                        request.idempotencyKey(),
                        request.requestUserType(),
                        request.requestUserId(),
                        request.requestReason());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.from(chargeCancelUseCase.cancel(cancelCommand)));
    }
}
