package com.codeit.sb02mplteam2.domain.social.dto;

import java.util.List;

public record CursorPageResponseDirectMessageDto(
    List<DirectMessageResponse> items,
    boolean hasNext,
    boolean hasPrevious,
    String startCursor,
    String endCursor
) {

}
