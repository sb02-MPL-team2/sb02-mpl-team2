package com.codeit.sb02mplteam2.domain.content.dto;

public record ContentDto(
    Long id,
    String title,
    String description,
    String category,
    String binaryContentUrl,
    Double totalRating,
    Integer reviewCount
) {
}
