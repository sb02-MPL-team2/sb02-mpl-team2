package com.codeit.sb02mplteam2.domain.content.batch;

import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateMoviesTasklet;
import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateTvsTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TmdbContentBatchJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job tmdbContentUpdateJob(UpdateMoviesTasklet updateMoviesTasklet, UpdateTvsTasklet updateTvsTasklet) {
    return new JobBuilder("tmdbContentUpdateJob", jobRepository)
        .start(updateMoviesStep(updateMoviesTasklet))
        .next(updateTvsStep(updateTvsTasklet))
        .build();
  }

  @Bean
  public Step updateMoviesStep(UpdateMoviesTasklet updateMoviesTasklet) {
    return new StepBuilder("updateMoviesStep", jobRepository)
        .tasklet(updateMoviesTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step updateTvsStep(UpdateTvsTasklet updateTvsTasklet) {
    return new StepBuilder("updateTvsStep", jobRepository)
        .tasklet(updateTvsTasklet, transactionManager)
        .build();
  }
}