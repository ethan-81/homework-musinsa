package com.homework.musinsa.domain.code;


import java.util.Arrays;

/**
 * 데이터베이스나 외부 API 연동 시 사용될 '코드(Code)' 값을 가지는 Enum이 구현하는 인터페이스입니다.
 *
 * <p><b>[컨벤션]</b>
 *
 * <ul>
 *   <li>JPA의 {@literal @Enumerated(EnumType.STRING)}은 사용하지 않습니다. Enum 상수명 변경 시 데이터 정합성이 깨지기 때문입니다.
 *   <li>대신, 모든 Enum은 이 인터페이스를 구현하고, 불변의 문자열 '코드'를 갖습니다.
 *   <li>DB 저장/조회 시에는 {@code AbstractCodeValueConverter}를 상속한 컨버터를 사용하여 {@code getCode()}의 값을 사용합니다.
 *   <li>이를 통해, 프로그램 내부에서 사용하는 Enum 상수명과 외부로 노출되는 값을 분리하여 리팩터링 유연성을 확보합니다.
 * </ul>
 */
public interface CodeValue {
    /**
     * DB 또는 외부 시스템에 저장/전송될 고정된 코드 값을 반환합니다.
     *
     * @return 코드 값 문자열
     */
    String getCode();

    /**
     * 코드 값 문자열로부터 해당하는 Enum 상수를 찾아 반환하는 팩토리 메서드를 위한 헬퍼입니다.
     *
     * <p>각 Enum에서는 이 메서드를 호출하는 {@code public static from(String code)} 메서드를 구현해야 합니다.
     *
     * @param enumClass 대상 Enum의 클래스
     * @param code 찾고자 하는 코드 값
     * @param <T> {@code CodeValue}를 구현한 Enum 타입
     * @return 코드 값에 해당하는 Enum 상수
     * @throws IllegalArgumentException 유효하지 않은 코드 값일 경우
     */
    static <T extends Enum<T> & CodeValue> T from(Class<T> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Unsupported " + enumClass.getSimpleName() + " code: " + code));
    }
}
