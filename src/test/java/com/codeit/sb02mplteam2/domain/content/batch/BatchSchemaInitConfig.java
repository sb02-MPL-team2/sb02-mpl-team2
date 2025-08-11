package com.codeit.sb02mplteam2.domain.content.batch;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@TestConfiguration
class BatchSchemaInitConfig {
  @Bean
  Object initBatchSchema(JdbcTemplate jdbcTemplate) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
        new ClassPathResource("org/springframework/batch/core/schema-h2.sql")
    );
    populator.execute(jdbcTemplate.getDataSource());
    return new Object();
  }
}