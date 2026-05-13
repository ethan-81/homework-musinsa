package com.homework.musinsa.domain;

import com.homework.musinsa.adapter.in.exception.BusinessException;
import com.homework.musinsa.common.util.IdGenerator;
import com.homework.musinsa.domain.code.TransactionStatus;
import com.homework.musinsa.domain.code.TransactionType;
import com.homework.musinsa.domain.code.UserType;
import com.homework.musinsa.domain.error.ErrorConstant;
import com.homework.musinsa.domain.vo.Point;
import com.homework.musinsa.domain.vo.TransactionResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Builder(toBuilder = true)
public class PointTransaction {
    private final long id;
    private final String canonicalId;
    private final String userId;
    private final Point amount;
    private final TransactionType transactionType;
    private final ChannelInfo channelInfo;
    private final List<PointTransactionEvent> events;
    private final LocalDateTime transactedAt;

    public static TransactionResult createCharge(
            String userId,
            Point amount,
            String channelType,
            String channelTransactionId,
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason,
            LocalDateTime processAt,
            Point maxAmountToCharge) {

        if (amount.isGreaterThan(maxAmountToCharge)) {
            throw new IllegalArgumentException("Amount cannot exceed the maximum amount to charge");
        }

        return PointTransaction.create(
                userId,
                amount,
                TransactionType.CHARGE,
                channelType,
                channelTransactionId,
                idempotencyKey,
                requestUserType,
                requestUserId,
                requestReason,
                processAt);
    }

    public static TransactionResult createUse(
            String userId,
            Point amount,
            String channelType,
            String channelTransactionId,
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason,
            LocalDateTime processAt) {

        return PointTransaction.create(
                userId,
                amount,
                TransactionType.USE,
                channelType,
                channelTransactionId,
                idempotencyKey,
                requestUserType,
                requestUserId,
                requestReason,
                processAt);
    }

    private static TransactionResult create(
            String userId,
            Point amount,
            TransactionType transactionType,
            String channelType,
            String channelTransactionId,
            String idempotencyKey,
            UserType requestUserType,
            String requestUserId,
            String requestReason,
            LocalDateTime transactedAt) {

        if (amount.isZero()) {
            throw new BusinessException(
                    ErrorConstant.INVALID_CHARGE_POINT_AMOUNT,
                    "Amount cannot be zero");
        }

        IdGenerator.GeneratedId transactionId = IdGenerator.generateWith("PT");
        ChannelInfo channelInfo = ChannelInfo.of(channelType, channelTransactionId);
        RequestInfo requestInfo =
                RequestInfo.of(idempotencyKey, requestUserType, requestUserId, requestReason);

        PointTransactionEvent event =
                PointTransactionEvent.createApproveEvent(
                        transactionId.serial(),
                        amount,
                        requestInfo,
                        transactedAt);

        PointTransaction transaction =
                PointTransaction.builder()
                        .id(transactionId.serial())
                        .canonicalId(transactionId.canonical())
                        .userId(userId)
                        .amount(amount)
                        .transactionType(transactionType)
                        .channelInfo(channelInfo)
                        .events(List.of(event))
                        .transactedAt(transactedAt)
                        .build();

        return TransactionResult.of(transaction, event);
    }

    public TransactionResult chargeCancel(RequestInfo requestInfo, LocalDateTime processAt) {
        if (!this.transactionType.equals(TransactionType.CHARGE)) {
            throw new IllegalStateException("Transaction is not a charge transaction");
        }

        return this.cancel(this.amount, requestInfo, processAt);
    }

    public TransactionResult useCancel(Point amountToCancel, RequestInfo requestInfo, LocalDateTime processAt) {
        if (!this.transactionType.equals(TransactionType.USE)) {
            throw new IllegalStateException("Transaction is not a use transaction");
        }

        if (amountToCancel.isGreaterThan(this.amount)) {
            throw new IllegalArgumentException("Amount to cancel cannot exceed the transaction amount");
        }

        return this.cancel(amountToCancel, requestInfo, processAt);
    }

    private TransactionResult cancel(Point amountToCancel, RequestInfo requestInfo, LocalDateTime processAt) {
        Optional<PointTransactionEvent> alreadyCanceled = this.findCancelEventBy(requestInfo);

        if (alreadyCanceled.isPresent()) {
            return TransactionResult.alreadyProcessed(this, alreadyCanceled.get());
        }

        if (this.transactionType.equals(TransactionType.EXPIRE)) {
            throw new IllegalStateException("Expire transaction cannot be cancelled");
        }

        if (!this.isCancelable(amountToCancel)) {
            throw new IllegalStateException("Transaction cannot be cancelled");
        }

        int nextSequence = this.events.size() + 1;
        PointTransactionEvent cancelEvent =
                PointTransactionEvent.createCancelEvent(
                        nextSequence,
                        this.id,
                        amountToCancel,
                        requestInfo,
                        processAt);
        List<PointTransactionEvent> events = new ArrayList<>(this.events);
        events.add(cancelEvent);

        PointTransaction canceledTransaction =
                this.toBuilder()
                        .events(List.copyOf(events))
                        .build();

        return TransactionResult.of(canceledTransaction, cancelEvent);
    }

    public TransactionStatus getTransactionStatus() {
        if (this.events.isEmpty() || this.calculateTotalAmount().isZero()) {
            return TransactionStatus.READY;
        }

        Point totalCanceled = this.calculateTotalCanceledAmount();

        if (this.amount.isEqual(totalCanceled)) {
            return TransactionStatus.CANCELED;
        }

        if (totalCanceled.isZero()) {
            return TransactionStatus.COMPLETED;
        }

        return TransactionStatus.PARTIALLY_CANCELED;
    }

    private boolean isCancelable(Point amountToCancel) {
        if (amountToCancel.isZero()) {
            return false;
        }

        TransactionStatus currentStatus = this.getTransactionStatus();

        if (currentStatus.equals(TransactionStatus.READY) ||
                currentStatus.equals(TransactionStatus.CANCELED)) {
            return false;
        }

        Point totalCanceled = this.calculateTotalCanceledAmount();
        Point expectedTotalCanceledAmount = totalCanceled.add(amountToCancel);

        if (expectedTotalCanceledAmount.isGreaterThan(this.amount)) {
            return false;
        }

        return true;
    }

    private Point calculateTotalAmount() {
        return this.events.stream()
                .filter(PointTransactionEvent::isApproveEvent)
                .map(PointTransactionEvent::getAmount)
                .reduce(Point.zero(), Point::add);
    }

    private Point calculateTotalCanceledAmount() {
        return this.events.stream()
                .filter(PointTransactionEvent::isCanceledEvent)
                .map(PointTransactionEvent::getAmount)
                .reduce(Point.zero(), Point::add);
    }

    public boolean isAdminApproval() {
        PointTransactionEvent approveEvent = this.getApproveEvent();
        return approveEvent.getRequestInfo().isAdmin();
    }

    public PointTransactionEvent getApproveEvent() {
        List<PointTransactionEvent> approveEvents = this.events.stream()
                .filter(PointTransactionEvent::isApproveEvent)
                .toList();

        if (approveEvents.isEmpty()) {
            throw new IllegalStateException("No approve event found");
        }

        if (approveEvents.size() > 1) {
            throw new IllegalStateException("Multiple approve events found: " + approveEvents.size());
        }

        return approveEvents.getFirst();
    }

    private Optional<PointTransactionEvent> findCancelEventBy(RequestInfo requestInfo) {
        return this.events.stream()
                .filter(PointTransactionEvent::isCanceledEvent)
                .filter(e -> e.getRequestInfo().idempotencyKey().equals(requestInfo.idempotencyKey()))
                .findFirst();
    }
}
