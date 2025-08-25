package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbMovieDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("tmdbMovieProcessor")
public class TmdbMovieToContentRowProcessor implements ItemProcessor<TmdbMovieDto, ContentRow> {

  private static final String TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w1280";

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

    return new ContentRow(
        "tmdb",
        String.valueOf(movieDto.id()),
        title,
        description,
        "MOVIE",
        imageUrl,
        null
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