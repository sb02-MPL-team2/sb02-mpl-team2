package com.codeit.sb02mplteam2.domain.livewatch.dto;

import java.time.LocalDateTime;

public record LiveChatRoomStatsDto(
    Long chatRoomId,
    Long contentId,
    Long totalMessages,
    Integer currentParticipants,
    Integer peakParticipants,
    LocalDateTime createdAt,
    LocalDateTime lastMessageAt
) {}
