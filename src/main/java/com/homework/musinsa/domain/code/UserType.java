package com.homework.musinsa.domain.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType implements CodeValue {
    USER("user", "사용자"),
    ADMIN("admin", "관리자"),
    ;

    private final String code;
    private final String description;
}
