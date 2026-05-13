package com.homework.musinsa.domain.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType implements CodeValue {
    CHARGE("charge", "포인트 적립"),
    USE("use", "포인트 사용"),
    EXPIRE("expire", "포인트 만료");

    private final String code;
    private final String description;
}
