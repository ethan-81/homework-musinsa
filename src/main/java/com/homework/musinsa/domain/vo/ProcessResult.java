package com.homework.musinsa.domain.vo;

import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointTransactionDetail;

public record ProcessResult(
        PointDeposit deposit,
        PointTransactionDetail detail,
        Point amountToProcess,
        Point processedAmount) {

    public ProcessResult {
        if (deposit == null) {
            throw new IllegalArgumentException("deposit must not be null");
        }

        if (detail == null) {
            throw new IllegalArgumentException("detail must not be null");
        }

        if (amountToProcess == null) {
            throw new IllegalArgumentException("amountToProcess must not be null");
        }

        if (processedAmount == null) {
            throw new IllegalArgumentException("processedAmount must not be null");
        }

        if (!amountToProcess.isEqual(processedAmount)) {
            throw new IllegalArgumentException("amountToProcess must equal processedAmount");
        }
    }

    public static ProcessResult of(PointDeposit deposit, PointTransactionDetail detail, Point amountToProcess) {
        Point processedAmount = detail.processedAmount();
        return new ProcessResult(deposit, detail, amountToProcess, processedAmount);
    }

    public Point remainingAmount() {
        return amountToProcess.subtract(processedAmount);
    }
}
