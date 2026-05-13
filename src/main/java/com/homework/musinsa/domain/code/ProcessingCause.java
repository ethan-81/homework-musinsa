package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProcessingCause implements CodeValue {
    ORIGIN("origin", "원본 처리"),
    RESTORE("restore", "복원 처리"),
    ALTERNATIVE("alternative", "대체 처리"),
    ;

    private final String code;
    private final String description;
}
