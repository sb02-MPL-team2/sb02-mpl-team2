package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record CursorPageResponsePlayListDto(
    List<PlaylistSlimDto> content,
    Object nextCursor,
    int size,
    boolean hasNext
) {

}
