package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessingResult implements CodeValue {
    COMPLETE("complete", "완료"),
    EXPIRED_DEPOSIT("expired_deposit", "deposit이 만료 됨"),
    INSUFFICIENT_DEPOSIT("insufficient_deposit", "deposit 잔액 부족"),
    ;
    private final String code;
    private final String description;

}
