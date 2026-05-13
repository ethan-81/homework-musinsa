package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType implements CodeValue {
    FREE_POINT("free_point", "무료 포인트"),
    ADMIN_POINT("admin_point", "관리자 지급 포인트"),
    ;

    private final String code;
    private final String description;
}
