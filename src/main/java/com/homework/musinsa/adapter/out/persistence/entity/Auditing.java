package com.homework.musinsa.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public final class Auditing {
    @Getter
    @Embeddable
    public static class Immutable {
        @CreatedDate
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;
    }

    @Getter
    @Embeddable
    public static class Mutable {
        @CreatedDate
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @LastModifiedDate
        @Column private LocalDateTime updatedAt;
    }
}
