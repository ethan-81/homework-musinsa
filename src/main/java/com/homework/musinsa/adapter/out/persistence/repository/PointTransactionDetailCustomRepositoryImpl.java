package com.homework.musinsa.adapter.out.persistence.repository;


import com.homework.musinsa.adapter.out.persistence.entity.PointTransactionDetailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointTransactionDetailCustomRepositoryImpl
        implements PointTransactionDetailCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<PointTransactionDetailEntity> details, LocalDateTime createdAt) {
        if (details == null || details.isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO point_transaction_detail "
                        + "("
                        + "id, transaction_id, transaction_event_id, deposit_id, processed_amount, "
                        + "processing_cause, processing_type, original_transaction_detail_id, created_at"
                        + ")"
                        + "VALUES ("
                        + "?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?"
                        + ")";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        PointTransactionDetailEntity detail = details.get(i);
                        ps.setLong(1, detail.getId());
                        ps.setLong(2, detail.getTransactionId());
                        ps.setLong(3, detail.getTransactionEventId());
                        ps.setLong(4, detail.getDepositId());
                        ps.setLong(5, detail.getProcessedAmount());

                        ps.setString(6, detail.getProcessingCause().getCode());
                        ps.setString(7, detail.getProcessingType().getCode());
                        ps.setLong(8, detail.getOriginalTransactionDetailId());
                        ps.setTimestamp(9, Timestamp.valueOf(createdAt));
                    }

                    @Override
                    public int getBatchSize() {
                        return details.size();
                    }
                });
    }
}
