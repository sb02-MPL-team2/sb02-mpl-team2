package com.codeit.sb02mplteam2.domain.livewatch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
    @NotNull(message = "같이 보기 방 ID는 필수입니다")
    Long liveWatchRoomId,

    @NotBlank(message = "메시지 내용은 필수입니다")
    String content
) {

}