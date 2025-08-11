package com.codeit.sb02mplteam2.domain.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieApiResponseDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class TmdbServiceMockTest {

  @Mock
  private WebClient webClient;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @InjectMocks
  private TmdbService tmdbService;

  @Test
  void testGetTmdbMoviesMock() {

    TmdbMovieDto dummyMovie = new TmdbMovieDto(
        "테스트 영화", "테스트 설명", "2025-01-01", "/poster.jpg"
    );

    TmdbMovieApiResponseDto fakeResponse = new TmdbMovieApiResponseDto(List.of(dummyMovie));
    WebClient.RequestHeadersUriSpec<?> uriSpec =
        (WebClient.RequestHeadersUriSpec<?>) mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec<?> headersSpec =
        (WebClient.RequestHeadersSpec<?>) mock(WebClient.RequestHeadersSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TmdbMovieApiResponseDto.class))
        .thenReturn(Mono.just(fakeResponse));

    // 실행
    List<TmdbMovieDto> result = tmdbService.getTmdbMovies(ContentCategory.MOVIE);

    // 검증
    assertThat(result).hasSize(1);
    assertThat(result.get(0).title()).isEqualTo("테스트 영화");
  }
}