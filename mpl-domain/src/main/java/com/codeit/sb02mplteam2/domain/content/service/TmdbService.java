package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDetailDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class TmdbService {

  private final WebClient webClient;
  private final String apiKey;

  public TmdbService(@Qualifier("tmdbWebClient") WebClient webClient,
      @Value("${external-api.tmdb.api-key}") String apiKey) {
    this.webClient = webClient;
    this.apiKey = apiKey;
  }

  public List<TmdbMovieDto> getTmdbMovies(ContentCategory category, int page, LocalDate targetDate) {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(category.getPath())
              .queryParam("api_key", apiKey)
              .queryParam("language", "ko-KR")
              .queryParam("page", page)
              .queryParam("primary_release_date.gte", targetDate.toString())
              .build())
          .retrieve()
          .bodyToMono(TmdbMovieApiResponseDto.class)
          .map(TmdbMovieApiResponseDto::results)
          .block();
    } catch (WebClientResponseException e) {
      log.error("TMDB API 응답 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
      return List.of();
    }
  }

  public List<TmdbTvDto> getTmdbTvs(ContentCategory category, int page, LocalDate targetDate) {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(category.getPath())
              .queryParam("api_key", apiKey)
              .queryParam("language", "ko-KR")
              .queryParam("page", page)
              .queryParam("first_air_date.gte", targetDate.toString())
              .build())
          .retrieve()
          .bodyToMono(TmdbTvApiResponseDto.class)
          .map(TmdbTvApiResponseDto::results)
          .block();
    } catch (WebClientResponseException e) {
      log.error("TMDB API 응답 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
      return List.of();
    }
  }

  // runtime용
  public TmdbMovieDetailDto getMovieDetail(long movieId) {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/movie/{id}")
              .queryParam("api_key", apiKey)
              .queryParam("language", "ko-KR")
              .build(movieId))
          .retrieve()
          .bodyToMono(TmdbMovieDetailDto.class)
          .block();
    } catch (WebClientResponseException e) {
      log.error("TMDB 상세 영화 조회 오류: id={}, status={}, body={}", movieId, e.getStatusCode(), e.getResponseBodyAsString(), e);
      return null;
    }
  }
}
