package com.codeit.sb02mplteam2.domain.playlist.service;

public record SubscribeRequest(
    Long userId,
    Long playlistId
) {

}
