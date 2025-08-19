package com.codeit.sb02mplteam2.domain.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserSearchRequest(
    String keyword,

    @NotNull(message = "검색 필터는 null일 수 없습니다.")
    UserSearchFilter filter,

    Long cursorId,

    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 사이즈는 100을 초과할 수 없습니다.")
    int pageSize
) {
}
