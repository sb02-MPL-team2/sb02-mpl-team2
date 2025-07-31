package com.codeit.sb02mplteam2.domain.content.dto;

import lombok.Builder;

@Builder
public record ContentResponseDto(
    Long id,
    String title,
    String description,
    String category,
    String binaryContentUrl,
    Double totalRating,
    Integer reviewCount,
    Integer watchCount
) {
}
