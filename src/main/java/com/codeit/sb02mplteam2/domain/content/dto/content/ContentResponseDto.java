package com.codeit.sb02mplteam2.domain.content.dto.content;

import lombok.Builder;

@Builder
public record ContentResponseDto(
    Long id,
    String title,
    String description,
    String category,
    String imageUrl,
    Double totalRating,
    Integer reviewCount,
    Integer watchCount,
    Long roomId
) {
}
