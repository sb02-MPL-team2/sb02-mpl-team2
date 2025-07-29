package com.codeit.sb02mplteam2.domain.livewatch.dto;

import java.time.LocalDateTime;

public record LiveChatMessageDto(
    Long id,
    String content,
    LocalDateTime createdAt,
    Long userId,
    String userName,
    String userAvatar,
    Long chatRoomId
) {}