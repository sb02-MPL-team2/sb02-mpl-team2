package com.codeit.sb02mplteam2.domain.livewatch.dto.websocket;

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
}