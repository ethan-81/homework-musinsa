package com.homework.musinsa.adapter.in.exception;

import com.homework.musinsa.domain.error.ErrorConstant;

public class InvalidParameterException extends BaseException {
    public InvalidParameterException(ErrorConstant errorConstant) {
        super(errorConstant.getErrorCode(), errorConstant.getErrorMessage());
    }
}

