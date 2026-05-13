package com.homework.musinsa.application.usecase;

import com.homework.musinsa.application.port.dto.Result;
import com.homework.musinsa.application.port.in.UseUseCase;
import com.homework.musinsa.application.port.out.PointAccountPort;
import com.homework.musinsa.application.port.out.PointPolicyPort;
import com.homework.musinsa.application.port.out.PointTransactionPort;
import com.homework.musinsa.application.service.PointUseService;
import com.homework.musinsa.common.aop.lock.UserLock;
import com.homework.musinsa.domain.PointAccount;
import com.homework.musinsa.domain.PointPolicy;
import com.homework.musinsa.domain.PointTransaction;
import com.homework.musinsa.domain.PointTransactionEvent;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UseUseCaseImpl implements UseUseCase {
    private final PointTransactionPort pointTransactionPort;
    private final PointAccountPort pointAccountPort;
    private final PointPolicyPort pointPolicyPort;
    private final PointUseService pointUseService;
    private final Clock clock;

    @Override
    @UserLock(key = "#command.userId")
    @Transactional
    public Result use(UseCommand command) {
        Optional<PointTransaction> existing =
                pointTransactionPort.findDuplicateTransaction(command.userId(), command.idempotencyKey());
        if (existing.isPresent()) {
            PointTransaction transaction = existing.get();
            return Result.alreadyProcessed(transaction, transaction.getApproveEvent());
        }

        PointPolicy policy = this.getCurrentPolicyOrThrow();
        TransactionResult transactionResult = this.createUseTransaction(command);

        PointAccount account = this.getPointAccountOrThrow(command.userId());
        pointUseService.useAndPersist(transactionResult, account, policy);

        PointAccount updatedAccount =
                account.decreaseBalance(transactionResult.amountToProcess());

        this.persist(
                transactionResult.transaction(),
                transactionResult.event(),
                updatedAccount);

        return Result.of(transactionResult);
    }

    private void persist(
            PointTransaction transaction,
            PointTransactionEvent event,
            PointAccount account) {

        pointTransactionPort.saveTransaction(transaction, event);
        pointAccountPort.savePointAccount(account);
    }

    private PointAccount getPointAccountOrThrow(String userId) {
        return pointAccountPort.findPointAccountBy(userId)
                .orElseThrow(() -> new IllegalArgumentException("Point account not found"));
    }

    private TransactionResult createUseTransaction(UseCommand command) {
        return PointTransaction.createUse(
                command.userId(),
                Point.of(command.amount()),
                command.channelType(),
                command.channelTransactionId(),
                command.idempotencyKey(),
                command.requestUserType(),
                command.requestUserId(),
                command.requestReason(),
                LocalDateTime.now(clock));
    }

    private PointPolicy getCurrentPolicyOrThrow() {
        return pointPolicyPort.findActivePolicy()
                .orElseThrow(() -> new IllegalArgumentException("Point charge policy not found"));
    }
}
