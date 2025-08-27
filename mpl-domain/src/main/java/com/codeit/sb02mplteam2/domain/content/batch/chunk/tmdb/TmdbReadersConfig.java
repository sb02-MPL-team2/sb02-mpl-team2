package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.batch.chunk.BatchApiItemReader;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.BatchWatermarkRepository;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TmdbReadersConfig {

  @Bean
  @StepScope
  public BatchApiItemReader<TmdbMovieDto> tmdbMovieReader(
      TmdbService tmdbService,
      BatchWatermarkRepository watermarkRepository,
      @Value("#{jobParameters['category']}") String categoryParam,
      @Value("#{jobParameters['maxPages']}") Integer maxPagesParam,
      @Value("#{jobParameters['rateLimitMs']}") Integer rateLimitMsParam
  ) {
    log.info("[TMDB] MOVIE Reader 생성됨, category={}", categoryParam);

    boolean runThisStep = (categoryParam == null)
        || "MOVIE".equalsIgnoreCase(categoryParam)
        || "ALL".equalsIgnoreCase(categoryParam);
    if (!runThisStep) {
      return new BatchApiItemReader<>((p, d) -> java.util.Collections.<TmdbMovieDto>emptyList(),
          "tmdb.movies",
          watermarkRepository);
    }

    var reader = new BatchApiItemReader<TmdbMovieDto>(
        (page, targetDate) -> {
          log.info("[TMDB] 영화 API 호출 page={}, date={}", page, targetDate);
          return tmdbService.getTmdbMovies(ContentCategory.MOVIE, page, targetDate);
        },
        "tmdb.movies",
        watermarkRepository
    );

    if (maxPagesParam != null && maxPagesParam > 0) {
      reader.setMaxPages(maxPagesParam);
    } else {
      reader.setMaxPages(10);
    }

    if (rateLimitMsParam != null && rateLimitMsParam > 0) {
      reader.setRateLimitMs(rateLimitMsParam);
    } else {
      reader.setRateLimitMs(250);
    }

    return reader;
  }

  @Bean
  @StepScope
  public BatchApiItemReader<TmdbTvDto> tmdbTvReader(
      TmdbService tmdbService,
      BatchWatermarkRepository watermarkRepository,
      @Value("#{jobParameters['category']}") String categoryParam,
      @Value("#{jobParameters['maxPages']}") Integer maxPagesParam,
      @Value("#{jobParameters['rateLimitMs']}") Integer rateLimitMsParam
  ) {
    log.info("[TMDB] TV Reader 생성됨, category={}", categoryParam);

    boolean runThisStep = (categoryParam == null)
        || "TV".equalsIgnoreCase(categoryParam)
        || "ALL".equalsIgnoreCase(categoryParam);
    if (!runThisStep) {
      return new BatchApiItemReader<>(
          (p, d) -> java.util.Collections.<TmdbTvDto>emptyList(),
          "tmdb.tv",
          watermarkRepository);
    }

    var reader = new BatchApiItemReader<TmdbTvDto>(
        (page, targetDate) -> {
          log.info("[TMDB] TV API 호출 page={}, date={}", page, targetDate);
          return tmdbService.getTmdbTvs(ContentCategory.TV, page, targetDate);
        },
        "tmdb.tv.page",
        watermarkRepository
    );

    if (maxPagesParam != null && maxPagesParam > 0) {
      reader.setMaxPages(maxPagesParam);
    } else {
      reader.setMaxPages(10);
    }

    if (rateLimitMsParam != null && rateLimitMsParam > 0) {
      reader.setRateLimitMs(rateLimitMsParam);
    } else {
      reader.setRateLimitMs(250);
    }

    return reader;
  }
}