package com.homework.musinsa.common.config.database;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories("com.homework.musinsa.adapter.out.persistence.repository")
@EntityScan("com.homework.musinsa.adapter.out.persistence.entity")
public class JpaConfig {
}
