package com.codeit.sb02mplteam2.domain.recommendation;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RecommendBatchConfig {

  @Bean
  public Job RecommendJob(JobRepository jobRepository,
      Step RecommendStep,
      Step broadcastTopPlaylistsStep
  ) {
    return new JobBuilder("recommendJob", jobRepository)
        .start(RecommendStep)
        .next(broadcastTopPlaylistsStep)
        .build();
  }

  @Bean
  @StepScope
  public JpaPagingItemReader<Playlist> recommendItemReader(
      EntityManagerFactory entityManagerFactory,
      @Value("#{jobParameters['start']}") LocalDateTime start,
      @Value("#{jobParameters['end']}") LocalDateTime end
  ) {
    //SELECT DISTINCT를 사용하여 중복을 제거할 때는,
    // ORDER BY 절에 사용하는 모든 열(column)이 반드시 SELECT 목록에도 포함되어야 함
    String jpqlQuery = "SELECT p FROM Playlist p WHERE p.id IN " +
        "(SELECT DISTINCT h.playlist.id FROM PlaylistSubscriberHistory h " +
        "WHERE h.createdAt >= :start AND h.createdAt < :end) " +
        "ORDER BY p.id ASC";

    // 파라미터 설정
    Map<String, Object> parameters = Map.of(
        "start", start,
        "end", end
    );

    return new JpaPagingItemReaderBuilder<Playlist>()
        .name("recommendItemReader") // 각 Reader마다 고유한 이름 부여
        .entityManagerFactory(entityManagerFactory) // EntityManagerFactory 주입
        .pageSize(100) // 청크 크기와 동일하게 설정
        .queryString(jpqlQuery) // 실행할 JPQL 쿼리
        .parameterValues(parameters) // 쿼리에 바인딩할 파라미터
        .build();
  }

  @Bean
  public Step RecommendStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      JpaPagingItemReader<Playlist> recommendItemReader,
      ItemProcessor<Playlist, PlaylistScore> processor,
      ItemWriter<PlaylistScore> writer) {
    return new StepBuilder("recommendStep", jobRepository)
        .<Playlist, PlaylistScore>chunk(100, transactionManager)
        .reader(recommendItemReader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step broadcastTopPlaylistsStep(
      JobRepository jobRepository,
      Tasklet recommendTasklet, // RecommendTasklet Bean 주입
      PlatformTransactionManager transactionManager) {
    return new StepBuilder("broadcastTopPlaylistsStep", jobRepository)
        .tasklet(recommendTasklet, transactionManager)
        .build();
  }

}
