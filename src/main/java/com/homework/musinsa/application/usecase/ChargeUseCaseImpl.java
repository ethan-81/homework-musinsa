package com.homework.musinsa.application.usecase;

import com.homework.musinsa.adapter.in.exception.BusinessException;
import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.application.port.in.ChargeUseCase;
import com.homework.musinsa.application.port.out.PointAccountPort;
import com.homework.musinsa.application.port.out.PointDepositPort;
import com.homework.musinsa.application.port.out.PointPolicyPort;
import com.homework.musinsa.application.port.out.PointTransactionDetailPort;
import com.homework.musinsa.application.port.out.PointTransactionPort;
import com.homework.musinsa.common.aop.lock.UserLock;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionDetail;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.domain.error.ErrorConstant;
import com.homework.musinsa.domain.processor.PointProcessor;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.ProcessResult;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargeUseCaseImpl implements ChargeUseCase {
    private final PointTransactionPort pointTransactionPort;
    private final PointAccountPort pointAccountPort;
    private final PointDepositPort pointDepositPort;
    private final PointTransactionDetailPort pointTransactionDetailPort;
    private final PointPolicyPort pointPolicyPort;
    private final PointProcessor pointProcessor;
    private final Clock clock;

    @Override
    @UserLock(key = "#command.userId")
    @Transactional
    public Result charge(ChargeCommand command) {

        Optional<PointTransaction> existing =
                pointTransactionPort.findDuplicateTransaction(command.userId(), command.idempotencyKey());

        if (existing.isPresent()) {
            PointTransaction transaction = existing.get();
            return Result.alreadyProcessed(transaction, transaction.getApproveEvent());
        }

        PointPolicy policy = this.getCurrentPolicyOrThrow();
        TransactionResult transactionResult = this.createChargeTransaction(command, policy);

        PointAccount account = this.getOrCreatePointAccount(command.userId());
        ProcessResult processResult =
                this.processCharge(transactionResult, account, policy);
        PointAccount updatedAccount =
                account.increaseBalance(transactionResult.amountToProcess(), policy.maxHoldPoint());

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

    private PointPolicy getCurrentPolicyOrThrow() {
        return pointPolicyPort.findActivePolicy()
                .orElseThrow(() -> new IllegalArgumentException("Point charge policy not found"));
    }

    private TransactionResult createChargeTransaction(ChargeCommand command, PointPolicy policy) {
        return PointTransaction.createCharge(
                        command.userId(),
                        Point.of(command.amount()),
                        command.channelType(),
                        command.channelTransactionId(),
                        command.idempotencyKey(),
                        command.requestUserType(),
                        command.requestUserId(),
                        command.requestReason(),
                        LocalDateTime.now(clock),
                        policy.maxChargePoint());
    }

    private PointAccount getOrCreatePointAccount(String userId) {
        return pointAccountPort.findPointAccountBy(userId)
                .orElseGet(() -> PointAccount.create(userId));
    }

    private ProcessResult processCharge(TransactionResult transactionResult, PointAccount account, PointPolicy policy) {
        PointType pointType = transactionResult.transaction().isAdminApproval() ? PointType.ADMIN_POINT : PointType.FREE_POINT;

        PointProcessor.ChargeCommand command =
                new PointProcessor.ChargeCommand(
                        transactionResult.transaction().getId(),
                        transactionResult.event().getId(),
                        transactionResult.amountToProcess(),
                        transactionResult.processDate(),
                        account.getId(),
                        pointType,
                        policy.validPeriodInDays());

        return pointProcessor.charge(command);
    }
}
