package com.codeit.sb02mplteam2.domain.livewatch.dto.response;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import java.util.List;

public record ChatMessagePageResponse(
    List<ChatMessageDto> messages,
    Integer messageCount,
    String nextCursor,
    Boolean hasNext
) {

}