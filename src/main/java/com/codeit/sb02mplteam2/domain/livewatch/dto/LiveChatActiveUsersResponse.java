package com.codeit.sb02mplteam2.domain.livewatch.dto;

import java.util.List;
import java.time.LocalDateTime;

public record LiveChatActiveUsersResponse(
    Long chatRoomId,
    Long contentId,
    Integer totalCount,
    List<ActiveUserDto> users
) {
  public record ActiveUserDto(
      Long userId,
      String userName,
      String userAvatar,
      LocalDateTime joinedAt
  ) {}
}
