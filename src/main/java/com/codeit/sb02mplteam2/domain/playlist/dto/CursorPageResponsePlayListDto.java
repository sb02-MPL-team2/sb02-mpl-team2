package com.codeit.sb02mplteam2.domain.playlist.dto;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record CursorPageResponsePlayListDto(
    List<PlaylistDto> content,
    Object nextCursor,
    int size,
    boolean hasNext
) {

  public static CursorPageResponsePlayListDto of(Slice<Playlist> slice, Object nextCursor) {
    List<PlaylistDto> list = slice.getContent().stream().map(PlaylistDto::from).toList();
    return CursorPageResponsePlayListDto.builder()
        .content(list)
        .nextCursor(nextCursor)
        .size(slice.getSize())
        .hasNext(slice.hasNext())
        .build();
  }

}
