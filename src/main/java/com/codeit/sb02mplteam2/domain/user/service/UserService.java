package com.codeit.sb02mplteam2.domain.user.service;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import java.util.List;
import java.util.Optional;

public interface UserService {
  UserDto create(UserCreateRequest request, Optional<BinaryContent> profileCreateRequest);

  UserDto find(Long userId);

  List<UserDto> findAll();

  UserDto update(Long userId, UserUpdateRequest request,
      Optional<BinaryContent> profileCreateRequest);

  void delete(Long userId);
}
