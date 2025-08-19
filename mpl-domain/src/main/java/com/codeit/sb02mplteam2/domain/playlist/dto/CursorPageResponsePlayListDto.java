package com.codeit.sb02mplteam2.domain.playlist.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CursorPageResponsePlayListDto(
    List<PlaylistSlimDto> content,
    Object nextCursor,
    int size,
    boolean hasNext
) {

}
