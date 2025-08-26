package com.codeit.sb02mplteam2.domain.content.dto.content;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContentRow(
    String provider,
    String externalId,
    String title,
    String description,
    String category,
    String imageUrl,
    Integer runtime,
    LocalDate releaseDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}