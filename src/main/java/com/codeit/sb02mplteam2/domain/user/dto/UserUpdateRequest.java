package com.codeit.sb02mplteam2.domain.user.dto;

public record UserUpdateRequest(
  String newUsername,
  String newEmail,
  String newPassword
)
{
}
