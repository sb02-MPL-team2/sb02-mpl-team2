package com.codeit.sb02mplteam2.domain.content.dto.content;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;

public record SaveResultDto(
    ContentCategory category,
    int saved
) {
}
