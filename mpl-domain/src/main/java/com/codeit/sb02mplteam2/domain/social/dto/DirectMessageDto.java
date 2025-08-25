package com.codeit.sb02mplteam2.domain.social.dto;

import java.time.LocalDateTime;

public record DirectMessageDto(
    Long id,
    LocalDateTime createdAt,
    Long channelId,
    String content
) {

}
