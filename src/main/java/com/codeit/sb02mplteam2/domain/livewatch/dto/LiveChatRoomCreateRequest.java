package com.codeit.sb02mplteam2.domain.livewatch.dto;

import jakarta.validation.constraints.NotNull;

public record LiveChatRoomCreateRequest(
    @NotNull(message = "Content ID는 필수입니다")
    Long contentId
) {}