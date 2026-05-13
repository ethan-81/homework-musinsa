package com.homework.musinsa.application.service;

import com.homework.musinsa.application.port.out.PointDepositPort;
import com.homework.musinsa.application.port.out.PointTransactionDetailPort;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.domain.processor.PointProcessor;
import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.domain.vo.BulkProcessResult;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.common.pagination.PointDepositCursor;
import com.homework.musinsa.domain.vo.ProcessResult;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointUseService {
    private final PointDepositPort pointDepositPort;
    private final PointTransactionDetailPort pointTransactionDetailPort;
    private final PointProcessor pointProcessor;

    @Transactional(propagation = Propagation.MANDATORY)
    public void useAndPersist(
            TransactionResult transactionResult,
            PointAccount account,
            PointPolicy policy) {

        Point remainingAmount = Point.copyOf(transactionResult.amountToProcess());
        List<PointType> deductionOrder = policy.deductionOrderByPointType();

        for (PointType pointType : deductionOrder) {
            BulkProcessResult result =
                    this.useByTypeAndPersist(
                            transactionResult.transaction(),
                            transactionResult.event(),
                            remainingAmount,
                            transactionResult.processDate(),
                            account,
                            pointType);

            remainingAmount = result.remainingAmount();

            if (remainingAmount.isLessThanOrEqualTo(Point.zero())) {
                break;
            }
        }

        if (!remainingAmount.isZero()) {
            throw new IllegalStateException("Insufficient point");
        }
    }

    private BulkProcessResult useByTypeAndPersist(
                PointTransaction transaction,
                PointTransactionEvent event,
                Point amountToProcess,
                LocalDate processDate,
                PointAccount account,
                PointType pointType) {

        Point remainingAmount = Point.copyOf(amountToProcess);
        Point processedAmount = Point.zero();
        int processedCount = 0;
        CursorPager.Request<PointDepositCursor> cursor =
                CursorPager.Request.first(PointDepositCursor.first());

        while (remainingAmount.isGreaterThan(Point.zero())) {
            CursorPager.Result<PointDeposit, PointDepositCursor> depositsChunk =
                    pointDepositPort.findDepositForDeduct(
                            account.getId(),
                            pointType,
                            processDate,
                            cursor);

            if (!depositsChunk.hasItems()) {
                break;
            }

            List<PointDeposit> processedDeposits = new ArrayList<>();
            List<PointTransactionDetail> processedDetails = new ArrayList<>();

            for (PointDeposit depositToDeduct : depositsChunk.items()) {

                ProcessResult processResult =
                        this.processUse(
                                transaction,
                                event,
                                remainingAmount,
                                processDate,
                                depositToDeduct);

                processedDeposits.add(processResult.deposit());
                processedDetails.add(processResult.detail());
                processedAmount = processedAmount.add(processResult.processedAmount());
                remainingAmount = processResult.remainingAmount();
                processedCount++;

                if (remainingAmount.isLessThan(Point.zero())) {
                    throw new IllegalStateException("Insufficient point");
                }

                if (remainingAmount.isZero()) {
                    break;
                }
            }

            this.persist(processedDetails, processedDeposits);

            if (!depositsChunk.hasNext() || remainingAmount.isZero()) {
                break;
            }

            cursor = cursor.nextFrom(depositsChunk);
        }

        return BulkProcessResult.of(amountToProcess, processedAmount, processedCount);
    }

    private ProcessResult processUse(
                PointTransaction transaction,
                PointTransactionEvent event,
                Point amountToProcess,
                LocalDate processDate,
                PointDeposit depositToUse) {

        PointProcessor.UseCommand command =
                new PointProcessor.UseCommand(
                        transaction.getId(),
                        event.getId(),
                        amountToProcess,
                        processDate,
                        depositToUse);

        return pointProcessor.use(command);
    }

    private void persist(List<PointTransactionDetail> details, List<PointDeposit> deposits) {
        pointDepositPort.bulkBalanceUpdate(deposits);
        pointTransactionDetailPort.bulkCreate(details);
    }
}
