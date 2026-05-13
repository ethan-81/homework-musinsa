package com.homework.musinsa.application.port.out;

import com.homework.musinsa.domain.PointAccount;

import java.util.Optional;

public interface PointAccountPort {
    Optional<PointAccount> findPointAccountBy(String userId);

    void savePointAccount(PointAccount pointAccount);
}
