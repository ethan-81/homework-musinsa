package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionEventType implements CodeValue {
    APPROVE("approve", "처리 됨"),
    CANCEL("cancel", "취소 됨"),
    ;

    private final String code;
    private final String description;
}
