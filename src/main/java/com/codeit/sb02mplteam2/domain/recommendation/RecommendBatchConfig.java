package com.codeit.sb02mplteam2.domain.recommendation;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RecommendBatchConfig {

  @Bean
  public Job RecommendJob(JobRepository jobRepository,
      Step RecommendStep
  ) {
    return new JobBuilder("recommendJob", jobRepository)
        .start(RecommendStep)
        .build();
  }

  @Bean
  public Step RecommendStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<PlaylistItem> reader,
      ItemProcessor<PlaylistItem, PlaylistScore> processor,
      ItemWriter<PlaylistScore> writer) {
    return new StepBuilder("recommendStep", jobRepository)
        .<PlaylistItem, PlaylistScore>chunk(100, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

}
