package com.homework.musinsa.adapter.in.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;
    private String errorDetailMessage;
    private Throwable cause;

    public BaseException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorCode, String errorMessage, String errorDetailMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetailMessage = errorDetailMessage;
    }

    public BaseException(String errorCode, String errorMessage, Throwable cause) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.cause = cause;

        errorDetailMessage =
                cause instanceof BaseException
                        ? ((BaseException) cause).getErrorMessage()
                        : cause.getMessage();
    }
}
