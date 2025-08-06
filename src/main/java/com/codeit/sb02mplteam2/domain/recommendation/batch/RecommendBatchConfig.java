package com.codeit.sb02mplteam2.domain.recommendation.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RecommendBatchConfig {

  @Bean
  public Job RecommendJob(JobRepository jobRepository) {
//
//    return new JobBuilder("recommendJob", jobRepository)
//        .start()
//        .build();
    return null;
  }

  @Bean
  public Step RecommendStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager) {
//    return new StepBuilder("recommendStep", jobRepository)
//        .chunk()
//        .build();
    return null;
  }

}
