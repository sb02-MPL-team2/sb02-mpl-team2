package com.codeit.sb02mplteam2.domain.livewatch.dto.websocket;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchMessage;
import com.codeit.sb02mplteam2.domain.livewatch.entity.MessageType;
import java.time.LocalDateTime;

public record ChatMessageDto(
    Long id,
    String content,
    LocalDateTime sentAt,
    Long userId,
    String userName,
    MessageType messageType
) {

  public static ChatMessageDto fromEntity(LiveWatchMessage entity) {
    return new ChatMessageDto(
        entity.getId(),
        entity.getContent(),
        entity.getSentAt(),
        entity.getUser().getId(),
        entity.getUser().getUsername(),
        entity.getMessageType()
    );
  }
}