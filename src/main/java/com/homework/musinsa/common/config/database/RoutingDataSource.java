package com.homework.musinsa.common.config.database;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Map;

public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final String READ_WRITE = "read-write";
    private static final String READ_ONLY = "read-only";

    private final DataSource defaultDataSource;
    private final Map<Object, Object> dataSourceMap;

    public static RoutingDataSource initDataSource(
            DataSource readWriteDataSource, DataSource readOnlyDataSource) {
        RoutingDataSource routingDataSource =
                new RoutingDataSource(readWriteDataSource, readOnlyDataSource);
        routingDataSource.setDataSource();
        return routingDataSource;
    }

    public RoutingDataSource(DataSource readWriteDataSource, DataSource readOnlyDataSource) {
        super();
        this.defaultDataSource = readWriteDataSource;
        this.dataSourceMap =
                Map.ofEntries(
                        Map.entry(RoutingDataSource.READ_WRITE, readWriteDataSource),
                        Map.entry(RoutingDataSource.READ_ONLY, readOnlyDataSource));
    }

    private void setDataSource() {
        super.setTargetDataSources(this.dataSourceMap);
        super.setDefaultTargetDataSource(this.defaultDataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? RoutingDataSource.READ_ONLY
                : RoutingDataSource.READ_WRITE;
    }
}
