package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.batch.chunk.BatchApiItemReader;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
public class TmdbBatchConfig {

  @Bean
  public Step importTmdbMoviesStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      @Qualifier("tmdbMovieReader") BatchApiItemReader<TmdbMovieDto> movieReader,
      @Qualifier("tmdbMovieProcessor") ItemProcessor<TmdbMovieDto, ContentRow> movieProcessor,
      @Qualifier("tmdbContentWriter") ItemWriter<ContentRow> contentWriter
  ) {
    var txAttr = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
    txAttr.setTimeout(30);

    return new StepBuilder("importTmdbMoviesStep", jobRepository)
        .<TmdbMovieDto, ContentRow>chunk(200, transactionManager)
        .transactionAttribute(txAttr)
        .reader(movieReader)
        .processor(movieProcessor)
        .writer(contentWriter)
        .build();
  }

  @Bean
  public Step importTmdbTvStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      @Qualifier("tmdbTvReader") BatchApiItemReader<TmdbTvDto> tvReader,
      @Qualifier("tmdbTvProcessor") ItemProcessor<TmdbTvDto, ContentRow> tvProcessor,
      @Qualifier("tmdbContentWriter") ItemWriter<ContentRow> contentWriter
  ) {
    var txAttr = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
    txAttr.setTimeout(30);

    return new StepBuilder("importTmdbTvStep", jobRepository)
        .<TmdbTvDto, ContentRow>chunk(200, transactionManager)
        .transactionAttribute(txAttr)
        .reader(tvReader)
        .processor(tvProcessor)
        .writer(contentWriter)
        .build();
  }

  @Bean
  public Job importTmdbJob(JobRepository jobRepository,
      @Qualifier("importTmdbMoviesStep") Step movies,
      @Qualifier("importTmdbTvStep") Step tv) {
    return new JobBuilder("importTmdbJob", jobRepository)
        .start(movies)
        .next(tv)
        .build();
  }
}