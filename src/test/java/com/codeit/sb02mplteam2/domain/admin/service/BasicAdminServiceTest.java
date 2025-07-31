package com.codeit.sb02mplteam2.domain.admin.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicAdminServiceTest {

  @InjectMocks
  private BasicAdminService adminService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private UserMapper userMapper;

  @Test
  @DisplayName("사용자 역할 변경 성공 테스트")
  void updateUserRole_Success() {
    // given
    Long userId = 1L;
    RoleUpdateRequest request = new RoleUpdateRequest(Role.ADMIN);
    User mockUser = new User("testUser", "test@test.com", "password", null);
    UserDto expectedDto = new UserDto(userId, null, "testUser", 5, 10);

    // Mock 객체 행동 정의
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(followRepository.countByToUser(mockUser)).willReturn(5);
    given(followRepository.countByFromUser(mockUser)).willReturn(10);
    given(userMapper.toDto(mockUser, 5, 10)).willReturn(expectedDto);

    // when 테스트 실행
    UserDto actualDto = adminService.updateUserRole(userId, request);

    // then (결과 검증)
    assertThat(actualDto).isNotNull();
    assertThat(actualDto.id()).isEqualTo(userId);
    assertThat(mockUser.getRole()).isEqualTo(Role.ADMIN);

    // Mock 객체가 예상대로 호출되었는지 검증
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("사용자 역할 변경 실패 - 사용자를 찾을 수 없는 경우")
  void updateUserRole_Fail_UserNotFound() {
    // given (테스트 준비)
    Long nonExistentUserId = 999L;
    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> {
      adminService.updateUserRole(nonExistentUserId, new RoleUpdateRequest(Role.ADMIN));
    });
  }
}
