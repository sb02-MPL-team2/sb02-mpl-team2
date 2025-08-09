package com.codeit.sb02mplteam2.domain.user.dto;

import com.codeit.sb02mplteam2.domain.user.entity.Role;

public record UserDto(
    Long id,
    String email,
    String username,
    String profileUrl,
    Role role,
    boolean isLocked,
    boolean isDeleted,
    int followerCount,
    int followingCount
) {
}
