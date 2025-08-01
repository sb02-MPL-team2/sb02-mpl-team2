package com.codeit.sb02mplteam2.domain.admin.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.user.UserNotFoundException;
import com.codeit.sb02mplteam2.security.MplUserDetail;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

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

  @Mock
  private SessionRegistry sessionRegistry;

  private User mockUser;
  private Long userId;
  private Long nonExistentUserId;
  private String username;

  @BeforeEach
  void setUp() {
    userId = 1L;
    nonExistentUserId = 999L;
    username = "testUser";
    mockUser = new User(username, "test@test.com", "password", null);
  }

  @Test
  @DisplayName("성공 - 사용자 역할 변경")
  void updateUserRole_Success() {
    // given
    RoleUpdateRequest request = new RoleUpdateRequest(Role.ADMIN);
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
  @DisplayName("실패 -사용자 역할 변경 - 사용자를 찾을 수 없는 경우")
  void updateUserRole_Fail_UserNotFound() {
    // given (테스트 준비)
    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> {
      adminService.updateUserRole(nonExistentUserId, new RoleUpdateRequest(Role.ADMIN));
    });
  }

  @Test
  @DisplayName("성공 - 사용자 계정 잠금 및 세션 만료")
  void lockUser_Success() {
    // given
    assertThat(mockUser.isLocked()).isFalse(); // 초기 상태는 잠겨있지 않음

    UserDetails userDetailsToLock = new MplUserDetail(mockUser);
    SessionInformation mockSessionInfo = mock(SessionInformation.class);

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(sessionRegistry.getAllPrincipals()).willReturn(List.of(userDetailsToLock));
    given(sessionRegistry.getAllSessions(userDetailsToLock, false)).willReturn(List.of(mockSessionInfo));

    // when
    adminService.lockUser(userId);

    // then
    assertThat(mockUser.isLocked()).isTrue(); // 계정이 잠겼는지 확인
    verify(userRepository).findById(userId);
    verify(sessionRegistry).getAllPrincipals();
    verify(sessionRegistry).getAllSessions(userDetailsToLock, false);
    verify(mockSessionInfo).expireNow(); // 세션 만료 메서드 호출 확인
  }

  @Test
  @DisplayName("실패 - 사용자 계정 잠금 - 사용자를 찾을 수 없는 경우")
  void lockUser_Fail_UserNotFound() {
    // given
    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> {
      adminService.updateUserRole(nonExistentUserId, new RoleUpdateRequest(Role.ADMIN));
    });
  }

  @Test
  @DisplayName("성공 - 사용자 계정 잠금 해제")
  void unlockUser_Success() {
    // given
    mockUser.lock();
    assertThat(mockUser.isLocked()).isTrue();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

    // when
    adminService.unlockUser(userId);

    // then
    assertThat(mockUser.isLocked()).isFalse();
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("실패 - 사용자 계정 잠금 해제 - 사용자를 찾을 수 없는 경우")
  void unlockUser_Fail_UserNotFound() {
    // given
    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    assertThrows(UserNotFoundException.class, () -> adminService.unlockUser(nonExistentUserId));
  }

}
