package com.homework.musinsa.application.usecase;

import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.application.port.in.UseCancelUseCase;
import com.homework.musinsa.application.port.out.PointAccountPort;
import com.homework.musinsa.application.port.out.PointPolicyPort;
import com.homework.musinsa.application.port.out.PointTransactionPort;
import com.homework.musinsa.application.service.PointUseCancelService;
import com.homework.musinsa.common.aop.lock.UserLock;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.RequestInfo;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UseCancelUseCaseImpl implements UseCancelUseCase {
    private final PointTransactionPort pointTransactionPort;
    private final PointAccountPort pointAccountPort;
    private final PointPolicyPort pointPolicyPort;
    private final PointUseCancelService pointUseCancelService;
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

        PointPolicy policy = this.getCurrentPolicyOrThrow();

        pointUseCancelService.cancelAndPersist(transactionResult, policy);

        PointAccount account = this.getPointAccountOrThrow(command.userId());
        PointAccount updatedAccount =
                account.increaseBalance(transactionResult.amountToProcess(), policy.maxHoldPoint());

        this.persist(transactionResult.transaction(), transactionResult.event(), updatedAccount);

        return Result.of(transactionResult);
    }

    private void persist(
            PointTransaction transaction,
            PointTransactionEvent event,
            PointAccount account) {

        pointTransactionPort.saveTransaction(transaction, event);
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
        return originTransaction.useCancel(
                Point.of(command.amount()),
                requestInfo,
                LocalDateTime.now(clock));
    }

    private PointPolicy getCurrentPolicyOrThrow() {
        return pointPolicyPort.findActivePolicy()
                .orElseThrow(() -> new IllegalArgumentException("Point charge policy not found"));
    }

    private PointAccount getPointAccountOrThrow(String userId) {
        return pointAccountPort.findPointAccountBy(userId)
                .orElseThrow(() -> new IllegalArgumentException("Point account not found"));
    }
}
