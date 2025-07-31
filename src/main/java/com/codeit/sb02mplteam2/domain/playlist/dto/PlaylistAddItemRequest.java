package com.codeit.sb02mplteam2.domain.playlist.dto;

public record PlaylistAddItemRequest(
    Long playListId,
    Long contentId
) {

}
