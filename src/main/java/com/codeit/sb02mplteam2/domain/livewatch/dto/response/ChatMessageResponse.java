package com.codeit.sb02mplteam2.domain.livewatch.dto.response;

import com.codeit.sb02mplteam2.domain.livewatch.entity.MessageType;
import java.time.LocalDateTime;

public record ChatMessageResponse(
    Long id,
    String content,
    LocalDateTime sentAt,
    Long userId,
    String userName,
    MessageType messageType
) {

}