package com.codeit.sb02mplteam2.domain.content.dto;

public record ContentRequestDto(
    String title,
    String category,
    String description,
    Long binaryContentId
) {
}
