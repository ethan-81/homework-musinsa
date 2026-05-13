package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionStatus implements CodeValue {
    READY("ready", "준비"),
    COMPLETED("completed", "완료"),
    PARTIALLY_CANCELED("partially_canceled", "부분취소"),
    CANCELED("canceled", "취소"),
    ;

    private final String code;
    private final String description;
}

