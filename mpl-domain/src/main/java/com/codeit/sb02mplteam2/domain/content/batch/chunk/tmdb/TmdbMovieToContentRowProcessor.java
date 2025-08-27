package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDetailDto;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component("tmdbMovieProcessor")
public class TmdbMovieToContentRowProcessor implements ItemProcessor<TmdbMovieDto, ContentRow> {

  private static final String TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w1280";
  private final TmdbService tmdbService;

  public TmdbMovieToContentRowProcessor(TmdbService tmdbService) {
    this.tmdbService = tmdbService;
  }

  @Override
  public ContentRow process(TmdbMovieDto movieDto) {
    if (movieDto == null || movieDto.id() == null) {
      return null;
    }

    String title = trimToNull(movieDto.title());
    if (title == null) {
      return null;
    }

    String description = trimToNull(movieDto.overview());
    String imageUrl = toImageUrl(movieDto.backdrop_path());
    LocalDateTime now = LocalDateTime.now();

    Integer runtime = null;
    TmdbMovieDetailDto detail = tmdbService.getMovieDetail(movieDto.id());
    if (detail != null && detail.runtime() != null && detail.runtime() > 0) {
      runtime = detail.runtime();
    }

    LocalDate releaseDate = null;
    if (movieDto.release_date() != null && !movieDto.release_date().isBlank()) {
      try {
        releaseDate = LocalDate.parse(movieDto.release_date().trim());
      } catch (Exception e) {
        log.warn("영화 {} release_date [{}] 파싱 실패 {}: {}", movieDto.id(), movieDto.release_date());
      }
    }


    return new ContentRow(
        "tmdb",
        String.valueOf(movieDto.id()),
        title,
        description,
        "MOVIE",
        imageUrl,
        runtime,
        releaseDate,
        now,
        now
    );
  }

  // 공백 제거
  private static String trimToNull(String text) {
    if (text == null) {
      return null;
    }
    String trimmed = text.trim();
    if (trimmed.isEmpty()) {
      return null;
    }
    return trimmed;
  }

  private static String toImageUrl(String path) {
    String imagePath = trimToNull(path);
    if (imagePath == null) {
      return null;
    }
    if (imagePath.startsWith("http")) {
      return imagePath;
    }
    if (!imagePath.startsWith("/")) {
      imagePath = "/" + imagePath;
    }
    return TMDB_IMG_BASE + imagePath;
  }
}