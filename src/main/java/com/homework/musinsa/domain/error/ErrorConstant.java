package com.homework.musinsa.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorConstant {
    UNKNOWN_ERROR("1", "알수 없는 오류 발생."),
    INVALID_REQUEST_DATA("2", "유효하지 않는 요청 데이터 입니다."),
    NOT_EXIST_POINT_TYPE_DATA("3", "포인트 타입 정보가 존재하지 않습니다."),
    INVALID_CHARGE_POINT_AMOUNT("4", "지급이 불가능한 금액입니다."),
    EXCEED_HOLDING_POINT_AMOUNT("5", "최대 보유 한도를 초과했습니다."),
    NOT_EXIST_CANCELABLE_POINT("6", "취소 가능한 포인트가 존재하지 않습니다."),
    IS_NOT_CANCELABLE("7", "취소 가능한 포인트가 아닙니다."),
    NOT_ENOUGH_TRANSACTION("8", "거래 가능한 포인트가 부족합니다."),
    DUPLICATE_TRANSACTION("9", "중복된 거래가 존재합니다."),
    ;

    private final String errorCode;
    private final String errorMessage;
}
