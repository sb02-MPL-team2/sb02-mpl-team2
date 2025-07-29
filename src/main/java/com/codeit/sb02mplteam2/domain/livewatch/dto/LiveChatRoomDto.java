package com.codeit.sb02mplteam2.domain.livewatch.dto;

import java.time.LocalDateTime;

public record LiveChatRoomDto(
    Long id,
    Long contentId,
    String contentTitle,
    LocalDateTime createdAt,
    Long totalMessages,
    Integer currentParticipants
) {}