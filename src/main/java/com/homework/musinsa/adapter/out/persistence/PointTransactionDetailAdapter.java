package com.homework.musinsa.adapter.out.persistence;


import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionDetailEntity;
import com.homework.musinsa.adapter.out.persistence.mapper.PointTransactionDetailMapper;
import com.homework.musinsa.adapter.out.persistence.repository.PointTransactionDetailRepository;
import com.homework.musinsa.application.port.out.PointTransactionDetailPort;
import com.homework.musinsa.common.pagination.CursorPager;
import com.homework.musinsa.common.pagination.PointTransactionDetailCursor;
import com.homework.musinsa.common.util.ListUtils;
import com.homework.musinsa.domain.PointTransactionDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PointTransactionDetailAdapter implements PointTransactionDetailPort {
    private static final int WRITE_CHUNK_SIZE = 1000;

    private final PointTransactionDetailRepository pointTransactionDetailRepository;

    @Override
    public Optional<PointTransactionDetail> findTransactionDetail(long transactionId, long eventId) {
        try {
            return pointTransactionDetailRepository
                    .findByTransactionIdAndTransactionEventId(transactionId, eventId)
                    .map(PointTransactionDetailMapper::toDomain);
        } catch (RuntimeException exception) {
            String message =
                    String.format(
                            "포인트 거래 상세 목록 조회(findTransactionDetail) 중 DB 오류 발생. 거래 ID : '%s', 이벤트 ID : '%s'",
                            transactionId, eventId);
            log.error(message, exception);
            throw exception;
        }
    }

    @Override
    public CursorPager.Result<PointTransactionDetail, PointTransactionDetailCursor> findTransactionDetailsWithCursor(
            long transactionId, long eventId, CursorPager.Request<PointTransactionDetailCursor> request) {
        try {
            List<PointTransactionDetailEntity> entities =
                    pointTransactionDetailRepository.findDetailsWithCursor(
                            transactionId,
                            eventId,
                            request.cursor().id(),
                            PageRequest.of(0, request.pageSize() + 1));

            if (entities.isEmpty()) {
                return CursorPager.Result.empty();
            }

            boolean hasNext = entities.size() > request.pageSize();
            List<PointTransactionDetailEntity> resultEntities =
                    hasNext ? entities.subList(0, request.pageSize()) : entities;
            List<PointTransactionDetail> result = resultEntities.stream().map(PointTransactionDetailMapper::toDomain).toList();

            return CursorPager.Result.of(result, hasNext, PointTransactionDetailCursor.from(result.getLast()));

        } catch (RuntimeException exception) {
            log.error("포인트 거래 상세 목록 조회(findTransactionDetailsWithCursor) 중 DB 오류 발생.", exception);
            throw exception;
        }
    }

    @Override
    public void saveTransactionDetail(PointTransactionDetail detail) {
        try {
            pointTransactionDetailRepository.save(PointTransactionDetailMapper.newEntityFrom(detail));
        } catch (RuntimeException exception) {
            log.error("포인트 거래 상세 저장(saveTransactionDetail) 중 예기치 못한 DB 오류 발생.", exception);
            throw exception;
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void bulkCreate(List<PointTransactionDetail> deposits) {
        if (deposits == null || deposits.isEmpty()) {
            return;
        }

        try {
            ListUtils.partitionList(deposits, WRITE_CHUNK_SIZE)
                    .forEach(
                            subList -> {
                                final LocalDateTime createAt = LocalDateTime.now();
                                List<PointTransactionDetailEntity> entities =
                                        subList.stream().map(PointTransactionDetailMapper::newEntityFrom).toList();
                                pointTransactionDetailRepository.bulkInsert(entities, createAt);
                            });
        } catch (RuntimeException exception) {
            log.error("포인트 거래 상세 대량 등록(bulkCreate) 처리 중 DB 오류 발생.", exception);
            throw exception;
        }
    }
}
