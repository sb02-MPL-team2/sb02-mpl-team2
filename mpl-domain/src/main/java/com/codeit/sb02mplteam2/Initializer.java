package com.codeit.sb02mplteam2;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDetailDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class Initializer implements ApplicationRunner {

  private final ContentRepository contentRepository;
  private final TmdbService tmdbService;

  private static final String TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w1280";

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (contentRepository.count() == 0) {
      int size = 20;
      List<Content> contents = new ArrayList<>();

      var movies = tmdbService.fetchPopularMovies(1, size);
      movies.forEach(m -> {
        LocalDate releaseDate = (m.release_date() != null && !m.release_date().isBlank())
            ? LocalDate.parse(m.release_date())
            : null;

        Integer runtime = 0;
        TmdbMovieDetailDto detail = tmdbService.getMovieDetail(m.id());
        if (detail != null && detail.runtime() != null) {
          runtime = detail.runtime();
        }

        contents.add(new Content(
            m.title(),
            m.overview(),
            ContentCategory.MOVIE,
            m.backdrop_path() != null ? TMDB_IMG_BASE + m.backdrop_path() : null,
            runtime,
            "TMDB",
            String.valueOf(m.id()),
            releaseDate,
            m.popularity()
        ));
      });

      var tvs = tmdbService.fetchPopularTvs(1, size);
      tvs.forEach(t -> {
        LocalDate releaseDate = (t.first_air_date() != null && !t.first_air_date().isBlank())
            ? LocalDate.parse(t.first_air_date())
            : null;

        contents.add(new Content(
            t.name(),
            t.overview(),
            ContentCategory.TV,
            t.backdrop_path() != null ? TMDB_IMG_BASE + t.backdrop_path() : null,
            0,
            "TMDB",
            String.valueOf(t.id()),
            releaseDate,
            t.popularity()
        ));
      });

      contents.stream()
          .sorted((a, b) -> Double.compare(b.getPopularity(), a.getPopularity()))
          .forEach(contentRepository::save);

      log.info("인기순 초기 적재 완료 (영화 20개 + TV 20개, 총 {}개)", contents.size());
    } else {
      log.info("DB에 데이터 존재 → 초기 적재 스킵");
    }
  }

}