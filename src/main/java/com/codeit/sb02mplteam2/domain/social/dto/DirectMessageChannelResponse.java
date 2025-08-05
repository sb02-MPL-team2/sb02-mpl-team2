package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import java.time.LocalDateTime;

public record DirectMessageChannelResponse(
    UserSlimDto receiverDto,
    Long channelId,
    Long senderId
) {

}
