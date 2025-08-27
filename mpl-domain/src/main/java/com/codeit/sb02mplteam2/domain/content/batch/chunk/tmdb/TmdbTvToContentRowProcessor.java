package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import com.codeit.sb02mplteam2.domain.content.dto.tmdb.TmdbTvDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component("tmdbTvProcessor")
public class TmdbTvToContentRowProcessor implements ItemProcessor<TmdbTvDto, ContentRow> {

  private static final String TMDB_IMG_BASE = "https://image.tmdb.org/t/p/w1280";

  @Override
  public ContentRow process(TmdbTvDto tvDto) {
    if (tvDto == null || tvDto.id() == null) {
      return null;
    }

    String title = trimToNull(tvDto.name());
    if (title == null) {
      return null;
    }

    String description = trimToNull(tvDto.overview());
    String imageUrl = toImageUrl(tvDto.backdrop_path());

    LocalDateTime now = LocalDateTime.now();

    LocalDate releaseDate = null;
    if (tvDto.first_air_date() != null && !tvDto.first_air_date().isBlank()) {
      try {
        releaseDate = LocalDate.parse(tvDto.first_air_date().trim());
      } catch (Exception e) {
        log.warn("TV {} first_air_date [{}] 파싱 실패", tvDto.id(), tvDto.first_air_date(), e);
      }
    }

    return new ContentRow(
        "tmdb",
        String.valueOf(tvDto.id()),
        title,
        description,
        "TV",
        imageUrl,
        null,
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