package com.homework.musinsa.domain.processor;

import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.ProcessResult;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PointProcessor {
    public ProcessResult charge(ChargeCommand command) {
        PointDeposit deposit =
                PointDeposit.create(
                        command.accountId(),
                        command.pointType(),
                        command.amountToProcess(),
                        command.processDate(),
                        command.daysToExpire());

        PointTransactionDetail detail =
                PointTransactionDetail.createChargeDetail(
                        command.transactionId(),
                        command.eventId(),
                        deposit.getId(),
                        command.amountToProcess());

        return ProcessResult.of(deposit, detail, command.amountToProcess());
    }

    public ProcessResult chargeCancel(ChargeCancelCommand command) {
        PointDeposit deposit =
                command.depositToCancel()
                        .decreaseBalance(command.amountToProcess(), command.processDate());

        PointTransactionDetail detail =
                PointTransactionDetail.createChargeCancelDetail(
                        command.transactionId(),
                        command.eventId(),
                        deposit.getId(),
                        command.amountToProcess(),
                        command.originDetail().id());

        return ProcessResult.of(deposit, detail, command.amountToProcess());
    }

    public ProcessResult use(UseCommand command) {
        PointDeposit depositToUse = command.depositToDeduct();
        Point availableAmount = depositToUse.calculateAmountToDecrease(command.amountToProcess());
        PointDeposit deposit =
                command.depositToDeduct()
                        .decreaseBalance(availableAmount, command.processDate());

        PointTransactionDetail detail =
                PointTransactionDetail.createUseDetail(
                        command.transactionId(),
                        command.eventId(),
                        deposit.getId(),
                        availableAmount);

        return ProcessResult.of(deposit, detail, command.amountToProcess());
    }

    public ProcessResult useCancel(UseCancelCommand command) {
        PointDeposit depositToCancel = command.depositToCancel();

        if (depositToCancel.isExpired()) {
            return alternativeCharge(command);
        }

        PointDeposit deposit =
                depositToCancel.increaseBalance(
                        command.amountToProcess(),
                        command.processDate());

        PointTransactionDetail detail =
                PointTransactionDetail.createUseCancelDetail(
                        command.transactionId(),
                        command.eventId(),
                        deposit.getId(),
                        command.amountToProcess(),
                        command.originDetail().id());

        return ProcessResult.of(deposit, detail, command.amountToProcess());
    }

    private ProcessResult alternativeCharge(UseCancelCommand command) {
        PointDeposit depositToCancel = command.depositToCancel();

        PointDeposit deposit =
                PointDeposit.create(
                        depositToCancel.getAccountId(),
                        depositToCancel.getPointType(),
                        command.amountToProcess(),
                        command.processDate(),
                        command.daysToExpire());

        PointTransactionDetail detail =
                PointTransactionDetail.createAlternativeChargeDetail(
                        command.transactionId(),
                        command.eventId(),
                        deposit.getId(),
                        command.amountToProcess(),
                        command.originDetail().id());

        return ProcessResult.of(deposit, detail, command.amountToProcess());
    }

    public record ChargeCommand(
            long transactionId,
            long eventId,
            Point amountToProcess,
            LocalDate processDate,

            long accountId,
            PointType pointType,
            int daysToExpire) { }

    public record ChargeCancelCommand(
            long transactionId,
            long eventId,
            Point amountToProcess,
            LocalDate processDate,

            PointTransactionDetail originDetail,
            PointDeposit depositToCancel) {}

    public record UseCommand(
            long transactionId,
            long eventId,
            Point amountToProcess,
            LocalDate processDate,

            PointDeposit depositToDeduct) {}

    public record UseCancelCommand(
            long transactionId,
            long eventId,
            Point amountToProcess,
            LocalDate processDate,

            PointTransactionDetail originDetail,
            PointDeposit depositToCancel,
            int daysToExpire) {}

}
