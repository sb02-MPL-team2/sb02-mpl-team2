package com.codeit.sb02mplteam2.domain.auth.service;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;

public interface AuthService {

  UserDto initAdmin();

  void createPasswordResetTokenForUser(String userEmail);

  void resetPassword(String token, String newPassword);
}
