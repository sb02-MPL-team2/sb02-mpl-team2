package com.codeit.sb02mplteam2.domain.playlist.dto.request;

public record PlaylistCreateRequest(
    Long userId,
    String title,
    String description) {

}
