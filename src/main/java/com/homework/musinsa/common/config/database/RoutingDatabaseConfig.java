package com.homework.musinsa.common.config.database;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class RoutingDatabaseConfig {
    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") final DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier("readWriteDataSource") final DataSource readWriteDataSource,
            @Qualifier("readOnlyDataSource") final DataSource readOnlyDataSource) {
        return RoutingDataSource.initDataSource(readWriteDataSource, readOnlyDataSource);
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read-write.hikari")
    public DataSource readWriteDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read-only.hikari")
    public DataSource readOnlyDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
