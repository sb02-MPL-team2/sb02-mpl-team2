package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PlaylistSlimDto(
    Long id,
    LocalDateTime updatedAt,
    int subscribeCount,
    int totalContent,
    String summary
) {

  public static PlaylistSlimDto from(Playlist playlist, String summary) {
    return PlaylistSlimDto.builder()
        .id(playlist.getId())
        .updatedAt(playlist.getUpdatedAt())
        .subscribeCount(playlist.getSubscribes().size())
        .totalContent(playlist.getItems().size())
        .build();

  }

}
