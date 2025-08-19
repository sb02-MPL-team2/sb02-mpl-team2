package com.codeit.sb02mplteam2.domain.content.dto.tmdb;

public record BatchResponseDto(
    Long executionId,
    String jobName,
    String status,
    String exitCode
) {
}
