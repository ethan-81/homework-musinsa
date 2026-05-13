package com.homework.musinsa.application.usecase;

import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.application.port.in.ChargeCancelUseCase;
import com.homework.musinsa.application.port.out.PointAccountPort;
import com.homework.musinsa.application.port.out.PointDepositPort;
import com.homework.musinsa.application.port.out.PointTransactionDetailPort;
import com.homework.musinsa.application.port.out.PointTransactionPort;
import com.homework.musinsa.common.aop.lock.UserLock;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.RequestInfo;
import com.homework.musinsa.domain.processor.PointProcessor;
import com.homework.musinsa.domain.vo.ProcessResult;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargeCancelUseCaseImpl implements ChargeCancelUseCase {
    private final PointTransactionPort pointTransactionPort;
    private final PointAccountPort pointAccountPort;
    private final PointDepositPort pointDepositPort;
    private final PointTransactionDetailPort pointTransactionDetailPort;

    private final PointProcessor pointProcessor;
    private final Clock clock;

    @Override
    @UserLock(key = "#command.userId")
    @Transactional
    public Result cancel(CancelCommand command) {
        PointTransaction originTransaction = this.getOriginTransactionOrThrow(command.userId(), command.transactionId());
        TransactionResult transactionResult = this.updateToCancel(originTransaction, command);

        if (transactionResult.isAlreadyProcessed()) {
            return Result.alreadyProcessed(transactionResult.transaction(), transactionResult.event());
        }

        PointTransactionDetail originChargeDetail = this.getOriginChargeDetailOrThrow(transactionResult);
        PointDeposit depositToCancel = this.getOriginChargeDepositOrThrow(originChargeDetail);

        ProcessResult processResult =
                this.processChargeCancel(transactionResult, depositToCancel, originChargeDetail);

        PointAccount account = this.getPointAccountOrThrow(command.userId());
        PointAccount updatedAccount =
                account.decreaseBalance(transactionResult.amountToProcess());

        this.persist(
                transactionResult.transaction(),
                transactionResult.event(),
                processResult.detail(),
                processResult.deposit(),
                updatedAccount);

        return Result.of(transactionResult);
    }

    private void persist(
            PointTransaction transaction,
            PointTransactionEvent event,
            PointTransactionDetail detail,
            PointDeposit deposit,
            PointAccount account) {

        pointTransactionPort.saveTransaction(transaction, event);
        pointDepositPort.savePointDeposit(deposit);
        pointTransactionDetailPort.saveTransactionDetail(detail);
        pointAccountPort.savePointAccount(account);
    }

    private PointTransaction getOriginTransactionOrThrow(String userId, String transactionId) {
        return pointTransactionPort.findByTransactionId(userId, transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    private TransactionResult updateToCancel(PointTransaction originTransaction, CancelCommand command) {
        RequestInfo requestInfo =
                RequestInfo.of(
                        command.idempotencyKey(),
                        command.requestUserType(),
                        command.requestUserId(),
                        command.requestReason());
        return originTransaction.chargeCancel(requestInfo, LocalDateTime.now(clock));
    }

    private PointAccount getPointAccountOrThrow(String userId) {
        return pointAccountPort.findPointAccountBy(userId)
                .orElseThrow(() -> new IllegalArgumentException("Point account not found"));
    }

    private PointTransactionDetail getOriginChargeDetailOrThrow(TransactionResult transactionResult) {
        PointTransaction transaction = transactionResult.transaction();

        return pointTransactionDetailPort.findTransactionDetail(
                    transaction.getId(),
                    transaction.getApproveEvent().getId())
                .orElseThrow(() -> new IllegalArgumentException("Approve detail not found"));
    }

    private PointDeposit getOriginChargeDepositOrThrow(PointTransactionDetail originChargeDetail) {
        return pointDepositPort.findDepositBy(originChargeDetail.depositId())
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found"));
    }

    private ProcessResult processChargeCancel(TransactionResult transactionResult, PointDeposit depositToCancel, PointTransactionDetail originChargeDetail) {
        PointProcessor.ChargeCancelCommand command =
                new PointProcessor.ChargeCancelCommand(
                        transactionResult.transaction().getId(),
                        transactionResult.event().getId(),
                        transactionResult.amountToProcess(),
                        transactionResult.processDate(),
                        originChargeDetail,
                        depositToCancel);

        return pointProcessor.chargeCancel(command);
    }
}
