package com.codeit.sb02mplteam2.domain.playlist;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
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
public class PlaylistBatchConfig {

  @Bean
  public Job PlaylistHistoryJob(JobRepository jobRepository, Step PlaylistHistoryStep) {
    return new JobBuilder("playlistJob", jobRepository)
        .start(PlaylistHistoryStep)
        .build();
  }

  @Bean
  public Step PlaylistHistoryStep(JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      ItemReader<Playlist> reader,
      ItemProcessor<Playlist, PlaylistSubscriberHistory> processor,
      ItemWriter<PlaylistSubscriberHistory> writer) {
    return new StepBuilder("playlistStep", jobRepository)
        .<Playlist, PlaylistSubscriberHistory>chunk(100, transactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }
}
