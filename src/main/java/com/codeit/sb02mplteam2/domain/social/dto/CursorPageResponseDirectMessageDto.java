package com.codeit.sb02mplteam2.domain.social.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseDirectMessageDto(
    List<DirectMessageResponse> items,
    LocalDateTime nextCursor,
    boolean hasNext
) {

}
