package com.homework.musinsa.application.service;

import com.homework.musinsa.application.port.out.PointDepositPort;
import com.homework.musinsa.application.port.out.PointTransactionDetailPort;
import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.common.pagination.PointTransactionDetailCursor;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.processor.PointProcessor;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.ProcessResult;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointUseCancelService {
    private final PointTransactionDetailPort pointTransactionDetailPort;
    private final PointDepositPort pointDepositPort;
    private final PointProcessor pointProcessor;

    @Transactional(propagation = Propagation.MANDATORY)
    public void cancelAndPersist(
            TransactionResult transactionResult,
            PointPolicy policy) {

        PointTransactionEvent originEvent = transactionResult.transaction().getApproveEvent();

        Point remainingAmount = Point.copyOf(transactionResult.amountToProcess());
        Point processedAmount = Point.zero();
        CursorPager.Request<PointTransactionDetailCursor> cursor =
                CursorPager.Request.first(PointTransactionDetailCursor.first());

        while (remainingAmount.isGreaterThan(Point.zero())) {
            CursorPager.Result<PointTransactionDetail, PointTransactionDetailCursor> detailChunk =
                    pointTransactionDetailPort.findTransactionDetailsWithCursor(
                            transactionResult.transaction().getId(),
                            originEvent.getId(),
                            cursor);

            List<PointDeposit> processedDeposits = new ArrayList<>();
            List<PointTransactionDetail> processedDetails = new ArrayList<>();
            Map<Long, PointDeposit> depositMap = this.loadDeposits(detailChunk.items());

            for (PointTransactionDetail originDetail : detailChunk.items()) {
                PointDeposit depositToCancel = depositMap.get(originDetail.depositId());

                if (depositToCancel == null) {
                    throw new IllegalArgumentException("Deposit not found");
                }

                ProcessResult processResult =
                        this.useCancel(
                                remainingAmount,
                                transactionResult,
                                originDetail,
                                depositToCancel,
                                policy);

                processedDeposits.add(processResult.deposit());
                processedDetails.add(processResult.detail());
                remainingAmount = processResult.remainingAmount();
                processedAmount = processedAmount.add(processResult.processedAmount());

                if (remainingAmount.isLessThan(Point.zero())) {
                    throw new IllegalStateException("Insufficient point");
                }

                if (remainingAmount.isZero()) {
                    break;
                }
            }

            this.persist(processedDetails, processedDeposits);

            if (!detailChunk.hasNext() || remainingAmount.isZero()) {
                break;
            }

            cursor = cursor.nextFrom(detailChunk);
        }

        if (!transactionResult.amountToProcess().equals(processedAmount)) {
            throw new IllegalStateException("Processed amount does not match transaction amount");
        }
    }

    private Map<Long, PointDeposit> loadDeposits(List<PointTransactionDetail> details) {
        List<Long> depositIds =
                details.stream()
                        .map(PointTransactionDetail::depositId)
                        .toList();

        return pointDepositPort.findDepositsByIds(depositIds)
                .stream()
                .collect(Collectors.toMap(PointDeposit::getId, deposit -> deposit));
    }

    private ProcessResult useCancel(
            Point amountToCancel,
            TransactionResult transactionResult,
            PointTransactionDetail originDetail,
            PointDeposit depositToCancel,
            PointPolicy policy) {

        Point cancelableAmount =
                amountToCancel.isGreaterThan(originDetail.processedAmount()) ?
                        originDetail.processedAmount() : Point.copyOf(amountToCancel);

        PointProcessor.UseCancelCommand command =
                new PointProcessor.UseCancelCommand(
                        transactionResult.transaction().getId(),
                        transactionResult.event().getId(),
                        cancelableAmount,
                        transactionResult.processDate(),
                        originDetail,
                        depositToCancel,
                        policy.validPeriodInDays());

        return pointProcessor.useCancel(command);
    }

    private void persist(List<PointTransactionDetail> details, List<PointDeposit> deposits) {
        List<PointDeposit> alternativeChargedDeposits = new ArrayList<>();
        List<PointDeposit> useRestoredDeposits = new ArrayList<>();

        Map<Long, PointDeposit> depositMap =
                deposits.stream().collect(Collectors.toMap(PointDeposit::getId, deposit -> deposit));

        for (PointTransactionDetail detail : details) {
            if (detail.isAlternativeCharge()) {
                alternativeChargedDeposits.add(depositMap.get(detail.depositId()));
            } else {
                useRestoredDeposits.add(depositMap.get(detail.depositId()));
            }
        }

        pointDepositPort.bulkBalanceUpdate(useRestoredDeposits);
        pointDepositPort.bulkCreate(alternativeChargedDeposits);
        pointTransactionDetailPort.bulkCreate(details);
    }
}
