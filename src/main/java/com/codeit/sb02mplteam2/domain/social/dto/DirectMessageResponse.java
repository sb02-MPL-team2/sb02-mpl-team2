package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import java.time.LocalDateTime;

public record DirectMessageResponse(
    UserSlimDto senderDto,
    Long directMessageId,
    Long channelId,
    String content,
    LocalDateTime createdAt
) {

}
