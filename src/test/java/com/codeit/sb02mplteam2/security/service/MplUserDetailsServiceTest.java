package com.codeit.sb02mplteam2.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.MplUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MplUserDetailsServiceTest {

  @InjectMocks
  private MplUserDetailsService mplUserDetailsService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  private User mockUser;
  private String username;
  private String email;
  private String nonExistentEmail = "nonExistent@test.com";
  private UserDto userDto;

  @BeforeEach()
  void setUp() {
    username = "testUser";
    email = "test@test.com";
    mockUser = new User(username, email, "password", null);
    userDto = new UserDto(1L, email, username, null,
        Role.USER, null,false, false, 0, 0);
  }

  @Test
  @DisplayName("성공 - 사용자 이름으로 UserDetails 조회")
  void loadUserByUsername_Success() {
    // given (준비)
    given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
    given(userMapper.toDto(mockUser)).willReturn(userDto);

    // when
    UserDetails userDetails = mplUserDetailsService.loadUserByUsername(email);

    // then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(email);
    assertThat(userDetails.getPassword()).isEqualTo("password");
    assertThat(userDetails.isAccountNonLocked()).isTrue();
    assertThat(userDetails.isEnabled()).isTrue();

    assertThat(userDetails.getAuthorities())
        .hasSize(1)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_" + Role.USER.name());

    verify(userRepository).findByEmail(email);
    verify(userMapper).toDto(mockUser);
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 사용자 이름으로 조회 시 예외 발생")
  void loadByUsername_Fail_UserNotFound() {
    // given
    given(userRepository.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

    // when
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
        mplUserDetailsService.loadUserByUsername(nonExistentEmail);
    });

    assertThat(exception.getMessage()).contains(nonExistentEmail);

    verify(userRepository).findByEmail(nonExistentEmail);
  }
}
