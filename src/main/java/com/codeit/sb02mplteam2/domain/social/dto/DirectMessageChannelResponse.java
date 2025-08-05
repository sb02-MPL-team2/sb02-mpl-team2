package com.codeit.sb02mplteam2.domain.social.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;

public record DirectMessageChannelResponse(
    UserSlimDto userDto,
    Long channelId,
    Long userId
) {

}
