package com.codeit.sb02mplteam2.domain.livewatch.dto;

import java.util.List;

public record LiveChatRoomJoinResponse(
    Long chatRoomId,
    Long contentId,
    List<LiveChatMessageDto> recentMessages,
    Integer currentParticipants,
    String webSocketUrl,
    String webSocketTopic
) {}

