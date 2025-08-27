package com.codeit.sb02mplteam2.domain.content.dto.content;

import java.time.LocalDate;
import java.util.List;

public record ScrollResponseDto<T>(
    List<T> items,
    boolean hasNext,
    LocalDate nextCursorDate,
    Long nextCursorId
) {}
