package com.codeit.sb02mplteam2.domain.user.service;

import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserCursorPageResponse;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
  UserDto create(UserCreateRequest request, Optional<MultipartFile> optionalMultipartFile);

  UserDto findById(Long userId);

  List<UserDto> findAll();

  UserCursorPageResponse<UserSearchDto> searchUsers(Long currentUserId, UserSearchRequest request);

  UserDto update(Long userId, UserUpdateRequest request,
      Optional<MultipartFile> optionalMultipartFile);

  void softDelete(Long userId);
}
