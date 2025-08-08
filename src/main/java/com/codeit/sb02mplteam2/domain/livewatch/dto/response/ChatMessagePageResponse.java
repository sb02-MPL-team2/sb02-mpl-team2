package com.codeit.sb02mplteam2.domain.livewatch.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessagePageResponse(
    List<ChatMessageResponse> messages,
    Integer messageCount,
    LocalDateTime nextCursor,
    Boolean hasNext
) {

}