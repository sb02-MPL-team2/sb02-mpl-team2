package com.codeit.sb02mplteam2.domain.user.service;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.user.UserException;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQueryService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;

  @Cacheable(value = "users", key = "#userId")
  public UserDto findByUserId(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    if(user.isDeleted() || user.isLocked()){
      log.warn("삭제되었거나 잠긴 사용자에 대한 조회 시도: userId={}", userId);
      throw UserNotFoundException.withId(userId);
    }

    return userMapper.toDto(user);
  }

  @CachePut(value = "users", key = "#userId")
  public UserDto refreshAndFindByUserId(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    if(user.isDeleted() || user.isLocked()){
      log.warn("삭제되었거나 잠긴 사용자에 대한 조회 시도: userId={}", userId);
      throw UserNotFoundException.withId(userId);
    }

    return userMapper.toDto(user);
  }
}
