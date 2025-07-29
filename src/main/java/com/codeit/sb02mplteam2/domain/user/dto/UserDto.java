package com.codeit.sb02mplteam2.domain.user.dto;

public record UserDto(
    Long id,
    String profileUrl,
    String username,
    int followerCount,
    int followingCount
) {
}
