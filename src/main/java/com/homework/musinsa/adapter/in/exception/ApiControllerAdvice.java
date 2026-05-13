package com.homework.musinsa.adapter.in.exception;


import com.homework.musinsa.adapter.in.dto.ErrorResponse;
import com.homework.musinsa.domain.error.ErrorConstant;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiControllerAdvice {
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                ErrorConstant.UNKNOWN_ERROR.getErrorCode(),
                                ErrorConstant.UNKNOWN_ERROR.getErrorMessage(),
                                exception.getMessage()));
    }

    @ExceptionHandler(InvalidParameterException.class)
    ResponseEntity<ErrorResponse> handleInvalidParameterException(
            InvalidParameterException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                exception.getErrorCode(),
                                exception.getErrorMessage(),
                                exception.getErrorDetailMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorResponse> handleInvalidParameterException(
            BusinessException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorResponse(
                                exception.getErrorCode(),
                                exception.getErrorMessage(),
                                exception.getErrorDetailMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorResponse(
                                ErrorConstant.INVALID_REQUEST_DATA.getErrorCode(),
                                ErrorConstant.INVALID_REQUEST_DATA.getErrorMessage(),
                                exception.getBindingResult().getFieldErrors().stream()
                                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                        .collect(Collectors.joining(" | "))));
    }
}
