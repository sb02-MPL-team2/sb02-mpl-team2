package com.codeit.sb02mplteam2.domain.user.dto;

public record UserFollowDto(
    Long id,
    String profileUrl,
    String username,
    int followerCount,
    int followingCount
) {

}
