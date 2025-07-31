package com.codeit.sb02mplteam2.domain.admin.service;

import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAdminService implements AdminService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public UserDto updateUserRole(Long userId, RoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.updateRole(request.role());

    int followerCount = followRepository.countByToUser(user);
    int followingCount= followRepository.countByFromUser(user);

    return userMapper.toDto(user, followerCount, followingCount);
  }
}
