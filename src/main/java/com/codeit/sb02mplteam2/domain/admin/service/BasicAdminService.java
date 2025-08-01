package com.codeit.sb02mplteam2.domain.admin.service;

import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAdminService implements AdminService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Transactional
  @Override
  public UserDto updateUserRole(Long userId, RoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.updateRole(request.role());

    int followerCount = followRepository.countByToUser(user);
    int followingCount = followRepository.countByFromUser(user);

    return userMapper.toDto(user, followerCount, followingCount);
  }

  @Transactional
  @Override
  public void lockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.lock();

    // Todo custom Login 적용 시 Username -> email로 바꾸기
    expireUserSessions(user.getUsername());
  }

  @Transactional
  @Override
  public void unlockUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    user.unlock();
  }

//  특정 사용자의 모든 활성 세션을 만료시킵니다.
  private void expireUserSessions(String username) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof UserDetails)
        .map(principal -> (UserDetails) principal)
        .filter(userDetails -> userDetails.getUsername().equals(username))
        .forEach(userDetails ->
            sessionRegistry.getAllSessions(userDetails, false)
                .forEach(sessionInformation -> sessionInformation.expireNow())
        );
  }
}
