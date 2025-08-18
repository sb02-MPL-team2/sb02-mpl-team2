package com.codeit.sb02mplteam2.domain.admin.service;

import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;

public interface AdminService {
  UserDto updateUserRole(Long userId, RoleUpdateRequest request);

  void lockUser(Long userId);

  void unlockUser(Long userId);
}
