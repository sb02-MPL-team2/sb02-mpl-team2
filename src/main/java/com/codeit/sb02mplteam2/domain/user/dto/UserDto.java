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
  public UserDto(Long id, String email, String username, int followerCount, int followingCount) {
    this(id, email, username, null, Role.USER, false, false, followerCount, followingCount);
  }

  public UserDto(Long id, String email, String username, String profileUrl, Role role,
      boolean isLocked, boolean isDeleted, int followerCount, int followingCount) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.profileUrl = profileUrl;
    this.role = role;
    this.isLocked = isLocked;
    this.isDeleted = isDeleted;
    this.followerCount = followerCount;
    this.followingCount = followingCount;
  }
}
