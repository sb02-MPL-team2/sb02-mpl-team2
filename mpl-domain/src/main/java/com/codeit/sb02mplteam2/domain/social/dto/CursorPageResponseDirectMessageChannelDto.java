package com.codeit.sb02mplteam2.domain.social.dto;

import java.util.List;

public record CursorPageResponseDirectMessageChannelDto(
    List<DirectMessageChannelResponse> items,
    Long nextCursor,
    boolean hasNext
) {

}
