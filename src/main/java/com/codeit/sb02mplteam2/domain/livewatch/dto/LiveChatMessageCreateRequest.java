package com.codeit.sb02mplteam2.domain.livewatch.dto;

import jakarta.validation.constraints.NotBlank;

public record LiveChatMessageCreateRequest(
    @NotBlank(message = "메시지 내용은 필수입니다")
    String content
) {}

