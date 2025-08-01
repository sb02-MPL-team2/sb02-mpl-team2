package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record PlaylistDto(
    Long id,
    Long userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String title,
    String description,
    int subscriberCount,
    List<PlaylistItem> items
) {

  public static PlaylistDto from(Playlist playlist) {
    Set<Subscribe> subscribes = playlist.getSubscribes();
    return PlaylistDto.builder()
        .id(playlist.getId())
        .userId(playlist.getUser().getId())
        .createdAt(playlist.getCreatedAt())
        .updatedAt(playlist.getUpdatedAt())
        .title(playlist.getTitle())
        .description(playlist.getDescription())
        .subscriberCount(subscribes.size())
        .items(playlist.getItems())
        .build();
  }

}
