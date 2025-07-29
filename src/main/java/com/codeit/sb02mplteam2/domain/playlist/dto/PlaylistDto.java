package com.codeit.sb02mplteam2.domain.playlist.dto;

import java.time.LocalDateTime;

public record PlaylistDto(
    Long id,
    String title,
    String description,
    LocalDateTime updatedAt,
    int subscriberCount
) {

}
