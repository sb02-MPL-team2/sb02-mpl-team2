package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
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
    UserSlimDto profile,
    List<PlaylistItemDto> items,
    List<ContentResponseDto> contentResponseDtoList
) {

  public static PlaylistDto of(Playlist playlist, UserSlimDto userSlimDto, List<PlaylistItemDto> playlistItemDtoList) {
    return of(playlist, userSlimDto, playlistItemDtoList, null);
  }

  public static PlaylistDto of(Playlist playlist, UserSlimDto userSlimDto, List<PlaylistItemDto> playlistItemDtoList, List<ContentResponseDto> contentResponseDtoList) {
    Set<Subscribe> subscribes = playlist.getSubscribes();
    return PlaylistDto.builder()
        .id(playlist.getId())
        .userId(userSlimDto.id())
        .createdAt(playlist.getCreatedAt())
        .updatedAt(playlist.getUpdatedAt())
        .title(playlist.getTitle())
        .description(playlist.getDescription())
        .subscriberCount(subscribes.size())
        .items(playlistItemDtoList)
        //DTO 정보들
        .profile(userSlimDto)
        .contentResponseDtoList(contentResponseDtoList)
        .build();
  }
}
