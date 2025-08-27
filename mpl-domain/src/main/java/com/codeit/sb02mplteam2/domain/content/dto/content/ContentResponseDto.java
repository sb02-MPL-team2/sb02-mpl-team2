package com.codeit.sb02mplteam2.domain.content.dto.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ContentResponseDto(
    Long id,
    String title,
    String description,
    String category,
    String imageUrl,
    Double totalRating,
    Long reviewCount,
    Long watchCount,
    Integer runtime,
    Long roomId,
    LocalDate releaseDate
) {
  @JsonProperty("runtime_in_seconds")
  public int getRuntimeInSeconds() {
    if (runtime == null) {
      return 0;
    }
    return runtime * 60;
  }
}
