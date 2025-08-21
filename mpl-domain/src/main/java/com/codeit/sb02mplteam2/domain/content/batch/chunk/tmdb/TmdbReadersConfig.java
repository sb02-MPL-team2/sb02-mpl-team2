package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.batch.chunk.BatchApiItemReader;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TmdbReadersConfig {

  @Bean
  @StepScope
  public BatchApiItemReader<TmdbMovieDto> tmdbMovieReader(
      TmdbService tmdbService,
      @Value("#{jobParameters['category']}") String categoryParam,
      @Value("#{jobParameters['maxPages']}") Integer maxPagesParam,
      @Value("#{jobParameters['rateLimitMs']}") Integer rateLimitMsParam
  ) {
    boolean runThisStep = (categoryParam == null) || "MOVIE".equalsIgnoreCase(categoryParam);
    if (!runThisStep) {
      return new BatchApiItemReader<>(p -> java.util.Collections.emptyList(), "tmdb.movies.skip");
    }

    var reader = new BatchApiItemReader<TmdbMovieDto>(
        page -> tmdbService.getTmdbMovies(ContentCategory.MOVIE, page),
        "tmdb.movies.page"
    );

    int defaultMaxPages = 10;
    int defaultRateLimit = 250;

    if (maxPagesParam != null && maxPagesParam > 0) {
      reader.setMaxPages(maxPagesParam);
    } else {
      reader.setMaxPages(defaultMaxPages);
    }
    if (rateLimitMsParam != null && rateLimitMsParam > 0) {
      reader.setRateLimitMs(rateLimitMsParam);
    } else {
      reader.setRateLimitMs(defaultRateLimit);
    }

    return reader;
  }

  @Bean
  @StepScope
  public BatchApiItemReader<TmdbTvDto> tmdbTvReader(
      TmdbService tmdbService,
      @Value("#{jobParameters['category']}") String categoryParam,
      @Value("#{jobParameters['maxPages']}") Integer maxPagesParam,
      @Value("#{jobParameters['rateLimitMs']}") Integer rateLimitMsParam
  ) {
    boolean runThisStep = (categoryParam == null) || "TV".equalsIgnoreCase(categoryParam);
    if (!runThisStep) {
      return new BatchApiItemReader<>(p -> java.util.Collections.emptyList(), "tmdb.tv.skip");
    }

    var reader = new BatchApiItemReader<TmdbTvDto>(
        page -> tmdbService.getTmdbTvs(ContentCategory.TV, page),
        "tmdb.tv.page"
    );

    int defaultMaxPages = 10;
    int defaultRateLimit = 250;

    if (maxPagesParam != null && maxPagesParam > 0) {
      reader.setMaxPages(maxPagesParam);
    } else {
      reader.setMaxPages(defaultMaxPages);
    }
    if (rateLimitMsParam != null && rateLimitMsParam > 0) {
      reader.setRateLimitMs(rateLimitMsParam);
    } else {
      reader.setRateLimitMs(defaultRateLimit);
    }

    return reader;
  }
}