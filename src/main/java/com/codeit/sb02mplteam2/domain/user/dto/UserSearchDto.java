package com.codeit.sb02mplteam2.domain.user.dto;

import com.codeit.sb02mplteam2.domain.user.entity.Role;

public record UserSearchDto(
    Long id,
    String username,
    String profileUrl,
    Role role,
    int followerCount,
    boolean isFollow
) {

}
