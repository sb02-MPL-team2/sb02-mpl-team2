package com.codeit.sb02mplteam2.domain.user.dto;

import com.codeit.sb02mplteam2.domain.user.entity.Role;

public record UserFollowDto(
    Long id,
    String profileUrl,
    String username,
    int followerCount,
    int followingCount,
    Role role
) {

}
