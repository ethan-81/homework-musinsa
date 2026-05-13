package com.homework.musinsa.adapter.in.exception;

import com.homework.musinsa.domain.error.ErrorConstant;

public class BusinessException extends BaseException {
    public BusinessException(ErrorConstant errorConstant, String errorDetailMessage) {
        super(errorConstant.getErrorCode(), errorConstant.getErrorMessage(), errorDetailMessage);
    }
}
