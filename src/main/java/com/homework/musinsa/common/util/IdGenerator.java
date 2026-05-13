package com.homework.musinsa.common.util;


import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;

/** 도메인 ID 생성을 책임지는 정적 유틸리티 클래스. */
public final class IdGenerator {
    // 실제 작업을 위임할 delegate. 기본적으로는 TsidGenerator를 사용합니다.
    private static final Generator delegate = new TsidGenerator();

    private IdGenerator() {} // 인스턴스화 방지

    // --- 공개 API ---
    public record GeneratedId(long serial, String canonical) {}

    public static GeneratedId generate() {
        return delegate.generate();
    }

    public static GeneratedId generateWith(String prefix) {
        return delegate.generateWith(prefix);
    }

    // 실제 로직을 수행할 인터페이스와 기본 구현체
    private interface Generator {
        GeneratedId generate();
        GeneratedId generateWith(String prefix);
    }

    private static class TsidGenerator implements Generator {
        @Override
        public GeneratedId generate() {
            Tsid tsid = TsidCreator.getTsid();
            return new GeneratedId(tsid.toLong(), null);
        }

        @Override
        public GeneratedId generateWith(String prefix) {
            Tsid tsid = TsidCreator.getTsid();
            long serial = tsid.toLong();
            String canonical = tsid.format(prefix +"_%S");

            return new GeneratedId(serial, canonical);
        }
    }
}

