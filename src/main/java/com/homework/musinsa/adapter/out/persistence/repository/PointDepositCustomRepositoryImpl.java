package com.homework.musinsa.adapter.out.persistence.repository;


import com.homework.musinsa.adapter.out.persistence.entity.PointDepositEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointDepositCustomRepositoryImpl implements PointDepositCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<PointDepositEntity> deposits, LocalDateTime createdAt) {
        if (deposits == null || deposits.isEmpty()) {
            return;
        }

        String sql =
                "INSERT INTO point_deposit "
                        + "("
                        + "id, account_id, point_type, expires_date, deposit_amount, "
                        + "balance, expired_amount, is_expired, created_at, updated_at, "
                        + "version"
                        + ")"
                        + "VALUES ("
                        + "?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ?, "
                        + "?"
                        + ")";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        PointDepositEntity deposit = deposits.get(i);
                        ps.setLong(1, deposit.getId());
                        ps.setLong(2, deposit.getAccountId());
                        ps.setString(3, deposit.getPointType().getCode());
                        ps.setDate(4, Date.valueOf(deposit.getExpiresDate()));
                        ps.setLong(5, deposit.getDepositAmount());

                        ps.setLong(6, deposit.getBalance());
                        ps.setLong(7, deposit.getExpiredAmount());
                        ps.setBoolean(8, deposit.isExpired());
                        ps.setTimestamp(9, Timestamp.valueOf(createdAt));
                        ps.setTimestamp(10, Timestamp.valueOf(createdAt));

                        ps.setLong(11, 0L);
                    }

                    @Override
                    public int getBatchSize() {
                        return deposits.size();
                    }
                });
    }

    @Override
    public void bulkBalanceUpdate(List<PointDepositEntity> deposits, LocalDateTime updatedAt) {
        if (deposits == null || deposits.isEmpty()) {
            return;
        }

        String sql =
                "UPDATE point_deposit "
                        + "SET "
                        + "balance = ?, "
                        + "updated_at = ?, "
                        + "version = version + 1 "
                        + "WHERE id = ?";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        PointDepositEntity deposit = deposits.get(i);
                        ps.setLong(1, deposit.getBalance());
                        ps.setTimestamp(2, Timestamp.valueOf(updatedAt));
                        ps.setLong(3, deposit.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return deposits.size();
                    }
                });
    }
}
