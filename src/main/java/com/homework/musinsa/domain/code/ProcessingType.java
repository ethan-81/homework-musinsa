package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessingType implements CodeValue {
    GRANT("grant", "적립"),
    DEDUCT("deduct", "차감"),
    ;

    private final String code;
    private final String description;
}
