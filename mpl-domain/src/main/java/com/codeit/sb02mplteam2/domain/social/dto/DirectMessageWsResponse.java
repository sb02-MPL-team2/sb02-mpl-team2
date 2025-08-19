package com.codeit.sb02mplteam2.domain.social.dto;

public record DirectMessageWsResponse(
    DirectMessageResponse message,
    Long receiverId
) {

}
