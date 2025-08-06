package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
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
    List<PlaylistItem> items,
    List<ContentResponseDto> contentResponseDtoList
) {

  public static PlaylistDto from(Playlist playlist) {
    User user = playlist.getUser();
    BinaryContent userProfile = user.getProfile();
    String profileUrl = null;
    if (userProfile != null) {
      profileUrl = userProfile.getUrl();
    }
    UserSlimDto slimDto = new UserSlimDto(user.getId(), profileUrl, user.getUsername());
    Set<Subscribe> subscribes = playlist.getSubscribes();
    return PlaylistDto.builder()
        .id(playlist.getId())
        .userId(user.getId())
        .createdAt(playlist.getCreatedAt())
        .updatedAt(playlist.getUpdatedAt())
        .title(playlist.getTitle())
        .description(playlist.getDescription())
        .subscriberCount(subscribes.size())
        .items(playlist.getItems())
        //DTO 정보들
        .profile(slimDto)
        .build();
  }
  public static PlaylistDto from(Playlist playlist,List<ContentResponseDto> contentResponseDtoList) {
    User user = playlist.getUser();
    BinaryContent userProfile = user.getProfile();
    String profileUrl = null;
    if (userProfile != null) {
      profileUrl = userProfile.getUrl();
    }
    UserSlimDto slimDto = new UserSlimDto(user.getId(), profileUrl, user.getUsername());
    Set<Subscribe> subscribes = playlist.getSubscribes();
    return PlaylistDto.builder()
        .id(playlist.getId())
        .userId(user.getId())
        .createdAt(playlist.getCreatedAt())
        .updatedAt(playlist.getUpdatedAt())
        .title(playlist.getTitle())
        .description(playlist.getDescription())
        .subscriberCount(subscribes.size())
        .items(playlist.getItems())
        //DTO 정보들
        .profile(slimDto)
        .contentResponseDtoList(contentResponseDtoList)
        .build();
  }
}
