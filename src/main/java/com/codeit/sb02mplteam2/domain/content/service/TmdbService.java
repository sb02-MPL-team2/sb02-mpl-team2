package com.codeit.sb02mplteam2.domain.content.service;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TmdbService {

  private final WebClient webClient;
  private final String apiKey;

  public TmdbService(@Qualifier("tmdbWebClient") WebClient webClient,
      @Value("${external-api.tmdb.api-key}") String apiKey) {
    this.webClient = webClient;
    this.apiKey = apiKey;
  }

  public List<TmdbMovieDto> getTmdbMovies(ContentCategory category) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(category.getPath())
            .queryParam("api_key", apiKey)
            .queryParam("language", "ko-KR")
            .build())
        .retrieve()
        .bodyToMono(TmdbMovieApiResponseDto.class)
        .map(TmdbMovieApiResponseDto::results)
        .block();
  }

  public List<TmdbTvDto> getTmdbTvs(ContentCategory category) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(category.getPath())
            .queryParam("api_key", apiKey)
            .queryParam("language", "ko-KR")
            .build())
        .retrieve()
        .bodyToMono(TmdbTvApiResponseDto.class)
        .map(TmdbTvApiResponseDto::results)
        .block();
  }
}
