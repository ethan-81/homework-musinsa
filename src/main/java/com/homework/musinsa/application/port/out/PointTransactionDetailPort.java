package com.homework.musinsa.application.port.out;

import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.common.pagination.PointTransactionDetailCursor;
import com.homework.musinsa.domain.PointTransactionDetail;

import java.util.List;
import java.util.Optional;

public interface PointTransactionDetailPort {
    Optional<PointTransactionDetail> findTransactionDetail(long transactionId, long eventId);

    CursorPager.Result<PointTransactionDetail, PointTransactionDetailCursor> findTransactionDetailsWithCursor(
            long transactionId,
            long eventId,
            CursorPager.Request<PointTransactionDetailCursor> request);

    void saveTransactionDetail(PointTransactionDetail detail);

    void bulkCreate(List<PointTransactionDetail> details);
}
