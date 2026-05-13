package com.homework.musinsa.domain;

import com.homework.musinsa.common.util.IdGenerator;
import com.homework.musinsa.domain.code.ProcessingCause;
import com.homework.musinsa.domain.code.ProcessingType;
import com.homework.musinsa.domain.vo.Point;

public record PointTransactionDetail(
    long id,
    long transactionId,
    long transactionEventId,
    long depositId,
    Point processedAmount,
    ProcessingCause processingCause,
    ProcessingType processingType,
    long originalTransactionDetailId) {

    public static PointTransactionDetail createChargeDetail(
            long transactionId,
            long transactionEventId,
            long depositId,
            Point chargeAmount) {

        long transactionDetailId = IdGenerator.generate().serial();

        return new PointTransactionDetail(
                transactionDetailId,
                transactionId,
                transactionEventId,
                depositId,
                chargeAmount,
                ProcessingCause.ORIGIN,
                ProcessingType.GRANT,
                transactionDetailId);
    }

    public static PointTransactionDetail createChargeCancelDetail(
            long transactionId,
            long transactionEventId,
            long depositId,
            Point cancelAmount,
            long originDetailId) {

        long transactionDetailId = IdGenerator.generate().serial();

        return new PointTransactionDetail(
                transactionDetailId,
                transactionId,
                transactionEventId,
                depositId,
                cancelAmount,
                ProcessingCause.RESTORE,
                ProcessingType.DEDUCT,
                originDetailId);
    }

    public static PointTransactionDetail createUseDetail(
            long transactionId,
            long transactionEventId,
            long depositId,
            Point useAmount) {

        long transactionDetailId = IdGenerator.generate().serial();

        return new PointTransactionDetail(
                transactionDetailId,
                transactionId,
                transactionEventId,
                depositId,
                useAmount,
                ProcessingCause.ORIGIN,
                ProcessingType.DEDUCT,
                transactionDetailId);
    }

    public static PointTransactionDetail createUseCancelDetail(
            long transactionId,
            long transactionEventId,
            long depositId,
            Point amountToCancel,
            long originDetailId) {

        long transactionDetailId = IdGenerator.generate().serial();

        return new PointTransactionDetail(
                transactionDetailId,
                transactionId,
                transactionEventId,
                depositId,
                amountToCancel,
                ProcessingCause.RESTORE,
                ProcessingType.GRANT,
                originDetailId);
    }

    public static PointTransactionDetail createAlternativeChargeDetail(
            long transactionId,
            long transactionEventId,
            long depositId,
            Point alternativeChargeAmount,
            long originDetailId) {

        long transactionDetailId = IdGenerator.generate().serial();

        return new PointTransactionDetail(
                transactionDetailId,
                transactionId,
                transactionEventId,
                depositId,
                alternativeChargeAmount,
                ProcessingCause.ALTERNATIVE,
                ProcessingType.GRANT,
                originDetailId);
    }

    public boolean isAlternativeCharge() {
        return this.processingCause.equals(ProcessingCause.ALTERNATIVE) &&
                this.processingType.equals(ProcessingType.GRANT);
    }
}
