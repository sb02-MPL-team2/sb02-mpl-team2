package com.codeit.sb02mplteam2.domain.content.dto.content;

public record ContentRow(
    String provider,
    String externalId,
    String title,
    String description,
    String category,
    String imageUrl,
    Integer runtime
) {}