package com.codeit.sb02mplteam2.domain.social.dto;

public record DirectMessageCreateRequest(
    Long senderId,
    Long channelId,
    String content
) {

}
