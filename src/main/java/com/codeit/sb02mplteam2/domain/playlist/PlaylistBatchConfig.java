package com.codeit.sb02mplteam2.domain.playlist;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PlaylistBatchConfig {

  @Bean
  public Job PlaylistHistoryJob(JobRepository jobRepository, Step PlaylistHistoryStep) {
    return new JobBuilder("PlaylistHistoryStep", jobRepository)
        .start(PlaylistHistoryStep)
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Playlist> playlistHistoryItemReader(
      EntityManagerFactory entityManagerFactory
  ) {
    String jpqlQuery = "SELECT p FROM Playlist p ORDER BY p.id ASC";

    return new JpaPagingItemReaderBuilder<Playlist>()
        .name("playlistHistoryItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString(jpqlQuery)
        .pageSize(100)
        .build();
  }

  @Bean
  public Step PlaylistHistoryStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      JpaPagingItemReader<Playlist> playlistHistoryItemReader,
      ItemProcessor<Playlist, PlaylistSubscriberHistory> processor,
      ItemWriter<PlaylistSubscriberHistory> writer) {
    return new StepBuilder("PlaylistHistoryStep", jobRepository)
        .<Playlist, PlaylistSubscriberHistory>chunk(100, transactionManager)
        .reader(playlistHistoryItemReader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}
