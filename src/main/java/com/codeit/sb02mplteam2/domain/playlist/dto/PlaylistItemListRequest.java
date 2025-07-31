package com.codeit.sb02mplteam2.domain.playlist.dto;

import java.util.List;

public record PlaylistItemListRequest(
    Long playListId,
    List<Long> contentIds
) {

}
