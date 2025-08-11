package com.codeit.sb02mplteam2.domain.admin.service;

import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAdminService implements AdminService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final JwtService jwtService;

  @Transactional
  @Override
  public UserDto updateUserRole(Long userId, RoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.updateRole(request.role());

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void lockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.lock();

    // userId 로 token 무효화 시키기
    jwtService.invalidateJwtSession(userId);
  }

  @Transactional
  @Override
  public void unlockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.unlock();
  }
}
