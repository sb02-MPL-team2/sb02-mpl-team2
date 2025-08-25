package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
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

  public List<TmdbMovieDto> getTmdbMovies(ContentCategory category, int page) {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(category.getPath())
              .queryParam("api_key", apiKey)
              .queryParam("language", "ko-KR")
              .queryParam("page", page)
              .build())
          .retrieve()
          .bodyToMono(TmdbMovieApiResponseDto.class)
          .map(TmdbMovieApiResponseDto::results)
          .block();
    } catch (WebClientResponseException e) {
      log.error("TMDB API 응답 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
      return List.of();
    } catch (Exception e) {
      log.error("TMDB API 호출 실패", e);
      return List.of();
    }
  }

  public List<TmdbTvDto> getTmdbTvs(ContentCategory category, int page) {
    try {
      return webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path(category.getPath())
              .queryParam("api_key", apiKey)
              .queryParam("language", "ko-KR")
              .queryParam("page", page)
              .build())
          .retrieve()
          .bodyToMono(TmdbTvApiResponseDto.class)
          .map(TmdbTvApiResponseDto::results)
          .block();
    } catch (WebClientResponseException e) {
      log.error("TMDB API 응답 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
      return List.of();
    } catch (Exception e) {
      log.error("TMDB API 호출 실패", e);
      return List.of();
    }
  }
}
