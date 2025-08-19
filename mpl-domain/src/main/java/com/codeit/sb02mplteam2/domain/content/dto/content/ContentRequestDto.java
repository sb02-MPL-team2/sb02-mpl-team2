package com.codeit.sb02mplteam2.domain.content.dto.content;

public record ContentRequestDto(
    String title,
    String category,
    String description,
    String imageUrl
) {
}
