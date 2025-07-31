package com.codeit.sb02mplteam2.domain.playlist.dto;

public record PlaylistUpdateRequest(
    String newTitle,
    String newDescription
) {

}
