package com.homework.musinsa.domain.vo;

import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.code.ProcessingResult;

public record DepositResult(
        PointDeposit deposit,
        Point amountToProcess,
        Point processedAmount,
        ProcessingResult processingResult) {
}
