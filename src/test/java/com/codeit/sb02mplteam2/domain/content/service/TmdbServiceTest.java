package com.codeit.sb02mplteam2.domain.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TmdbServiceTest {

  @Autowired
  private TmdbService tmdbService;

  @Test
  void testGetTmdbMoviesRealApi() {
    List<TmdbMovieDto> movies = tmdbService.getTmdbMovies(ContentCategory.MOVIE);

    assertThat(movies).isNotEmpty();
    movies.stream()
        .limit(5)
        .forEach(m -> System.out.println(m.title() + " | " + m.release_date()));
  }

  @Test
  void testGetTmdbTvsRealApi() {
    List<TmdbTvDto> tvs = tmdbService.getTmdbTvs(ContentCategory.TV);

    assertThat(tvs).isNotEmpty();
    tvs.stream()
        .limit(5)
        .forEach(t -> System.out.println(t.name() + " | " + t.first_air_date()));
  }

}
