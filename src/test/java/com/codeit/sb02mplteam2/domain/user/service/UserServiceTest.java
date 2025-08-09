package com.codeit.sb02mplteam2.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.binary.service.BinaryContentService;
import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private BasicUserService basicUserService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private BinaryContentService binaryContentService;

  @Mock
  private SessionRegistry sessionRegistry;

  private User mockUser;
  private Long userId;
  private Long nonExistentUserId;

  @BeforeEach
  void setUp() {
    userId = 1L;
    nonExistentUserId = 999L;
    mockUser = new User("testUser", "test@test.com", "password", null);
  }

  @Test
  @DisplayName("성공 - 회원가입")
  void create_Success() {
    // given
    UserCreateRequest request = new UserCreateRequest("newUser",
        "new@email.com", "newPassword");
    User newUser =  new User(request.username(), request.email(), "password", null);
    UserDto expectedDto = new UserDto(2L, request.email(), request.username(), null,
        Role.USER, false, false, 0, 0);

    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(passwordEncoder.encode(request.password())).willReturn("password");
    given(userRepository.save(any(User.class))).willReturn(newUser);
    given(userMapper.toDto(newUser)).willReturn(expectedDto);

    // when
    UserDto resultDto = basicUserService.create(request, Optional.empty());

    // then
    assertThat(resultDto).isNotNull();
    assertThat(resultDto.username()).isEqualTo(request.username());

    verify(userRepository).existsByUsername(request.username());
    verify(userRepository).existsByEmail(request.email());
    verify(passwordEncoder).encode(request.password());
    verify(userRepository).save(any(User.class));
    verify(userMapper).toDto(newUser);
  }

  @Test
  @DisplayName("실패 - 회원가입 (이메일 중복)")
  void create_Fail_WhenEmailExists() {
    // given
    UserCreateRequest request = new UserCreateRequest("newUser",
        "test@example.com", "password");
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // when & then
    UserException exception = assertThrows(UserException.class, () ->{
      basicUserService.create(request, Optional.empty());
    });

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("실패 - 회원가입 (사용자 이름 중복)")
  void create_Fail_WhenUsernameExists() {
    // given
    UserCreateRequest request = new UserCreateRequest("testUser",
        "test@example.com", "password");
    given(userRepository.existsByUsername(request.username())).willReturn(true);

    // when & then
    UserException exception = assertThrows(UserException.class, () -> {
      basicUserService.create(request, Optional.empty());
    });

    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("성공 - ID로 사용자 조회")
  void findById_Success() {
    // given
    UserDto expectedDto = new UserDto(userId, "test@test.com", "testUser", null,
        Role.USER, false, false, 0, 0);
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userMapper.toDto(mockUser)).willReturn(expectedDto);

    // when
    UserDto resultDto = basicUserService.findById(userId);

    // then
    assertThat(resultDto).isNotNull();
    assertThat(resultDto.id()).isEqualTo(userId);
    verify(userRepository).findById(userId);
    verify(userMapper).toDto(mockUser);
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 ID로 사용자 조회")
  void findById_Fail_UserNotFound() {
    //given
    given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

    // when & then
    UserException exception = assertThrows(UserException.class, () ->{
      basicUserService.findById(nonExistentUserId);
    });
    verify(userRepository).findById(nonExistentUserId);
  }

  @Test
  @DisplayName("성공 - 모든 사용자 조회")
  void findAll_Success() {
    // given
    User anotherUser = new User("anotherUser",
        "another@example.com", "pwd", null);
    given(userRepository.findAll()).willReturn(List.of(mockUser, anotherUser));

    // when
    List<UserDto> results = basicUserService.findAll();

    // then
    assertThat(results).hasSize(2);
    verify(userRepository).findAll();
    verify(userMapper, times(2)).toDto(any(User.class));
  }

  @Test
  @DisplayName("성공 - 사용자 수정")
  void update_Success() {
    // given
    UserUpdateRequest request = new UserUpdateRequest("updatedUser",
        "updated@example.com", "newPassword");
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(passwordEncoder.encode(request.newPassword())).willReturn("newEncodedPassword");

    // when
    basicUserService.update(userId, request, Optional.empty());

    // then
    assertThat(mockUser.getUsername()).isEqualTo(request.newUsername());
    assertThat(mockUser.getEmail()).isEqualTo(request.newEmail());
    assertThat(mockUser.getPassword()).isEqualTo("newEncodedPassword");
    verify(userRepository).findById(userId);
    verify(userRepository).existsByUsername(request.newUsername());
    verify(userRepository).existsByEmail(request.newEmail());
    verify(passwordEncoder).encode(request.newPassword());
  }

  @Test
  @DisplayName("성공 - 사용자 soft 삭제")
  void softDelete_Success() {
    // given
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    assertThat(mockUser.isDeleted()).isFalse();

    // when
    basicUserService.softDelete(userId);

    // then
    verify(userRepository).findById(userId);
    assertThat(mockUser.isDeleted()).isTrue();
  }
}
