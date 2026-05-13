package com.homework.musinsa.adapter.in;

import com.homework.musinsa.adapter.in.dto.ErrorResponse;
import com.homework.musinsa.adapter.in.dto.SuccessResponse;
import com.homework.musinsa.adapter.in.dto.UseCancelRequest;
import com.homework.musinsa.adapter.in.dto.UseRequest;
import com.homework.musinsa.application.port.in.UseCancelUseCase;
import com.homework.musinsa.application.port.in.UseUseCase;
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
@Tag(name = "포인트 사용", description = "포인트 사용 API")
public class PointUseController {
    private final UseUseCase useUseCase;
    private final UseCancelUseCase useCancelUseCase;

    @PostMapping(path = "homework/point/use", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "포인트 사용", description = "적립 된 포인트를 사용하는 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "사용 완료"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 요청 정보",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(
                            responseCode = "500",
                            description = "알수 없는 오류 발생",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
            })
    ResponseEntity<SuccessResponse> usePoint(
            @Valid @RequestBody final UseRequest request) {
        UseUseCase.UseCommand useCommand =
                new UseUseCase.UseCommand(
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
                .body(SuccessResponse.from(useUseCase.use(useCommand)));
    }

    @PostMapping(path = "homework/point/use/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "포인트 사용 취소", description = "사용 된 포인트를 (부분) 취소하는 API 입니다.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "사용 완료"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 요청 정보",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
                    @ApiResponse(
                            responseCode = "500",
                            description = "알수 없는 오류 발생",
                            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
            })
    ResponseEntity<SuccessResponse> useCancelPoint(
            @Valid @RequestBody final UseCancelRequest request) {
        UseCancelUseCase.CancelCommand cancelCommand =
                new UseCancelUseCase.CancelCommand(
                        request.userId(),
                        request.transactionId(),
                        request.amount(),
                        request.idempotencyKey(),
                        request.requestUserType(),
                        request.requestUserId(),
                        request.requestReason());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.from(useCancelUseCase.cancel(cancelCommand)));
    }
}
