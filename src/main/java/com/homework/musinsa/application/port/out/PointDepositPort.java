package com.homework.musinsa.application.port.out;

import com.homework.musinsa.domain.PointDeposit;
import com.homework.musinsa.domain.code.PointType;
import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.common.pagination.PointDepositCursor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PointDepositPort {
    Optional<PointDeposit> findDepositBy(long depositId);

    CursorPager.Result<PointDeposit, PointDepositCursor> findDepositForDeduct(
            long accountId,
            PointType pointType,
            LocalDate processedDate,
            CursorPager.Request<PointDepositCursor> request);

    List<PointDeposit> findDepositsByIds(List<Long> depositIds);

    void savePointDeposit(PointDeposit deposit);

    void bulkBalanceUpdate(List<PointDeposit> deposits);

    void bulkCreate(List<PointDeposit> deposits);
}
