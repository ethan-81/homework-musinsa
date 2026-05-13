package com.homework.musinsa.adapter.in.exception;

import com.homework.musinsa.domain.error.ErrorConstant;

public class UnexpectedException extends BaseException {
    public UnexpectedException(Throwable cause) {
        super(
                ErrorConstant.UNKNOWN_ERROR.getErrorCode(),
                ErrorConstant.UNKNOWN_ERROR.getErrorMessage(),
                cause);
    }
}
