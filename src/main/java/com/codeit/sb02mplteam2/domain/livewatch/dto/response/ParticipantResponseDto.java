package com.codeit.sb02mplteam2.domain.livewatch.dto.response;

import java.time.LocalDateTime;

public record ParticipantResponseDto(
    Long userId,
    String userName,
    String profileUrl,
    LocalDateTime joinedAt
) {
}