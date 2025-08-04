package com.codeit.sb02mplteam2.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
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

  private User mockUser;
  private String username;
  private String nonExistentUsername = "nonExistentUser";

  @BeforeEach()
  void setUp() {
    username = "testUser";
    mockUser = new User(username, "test@test.com", "password", null);
  }

  @Test
  @DisplayName("성공 - 사용자 이름으로 UserDetails 조회")
  void loadUserByUsername_Success() {
    // given (준비)
    given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));

    // when
    UserDetails userDetails = mplUserDetailsService.loadUserByUsername(username);

    // then
    assertThat(userDetails).isNotNull();
    assertThat(userDetails.getUsername()).isEqualTo(username);
    assertThat(userDetails.getPassword()).isEqualTo("password");
    assertThat(userDetails.isAccountNonLocked()).isTrue();

    assertThat(userDetails.getAuthorities())
        .hasSize(1)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_" + Role.USER.name());

    verify(userRepository).findByUsername(username);
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 사용자 이름으로 조회 시 예외 발생")
  void loadByUsername_Fail_UserNotFound() {
    // given
    given(userRepository.findByUsername(nonExistentUsername)).willReturn(Optional.empty());

    // when
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
        mplUserDetailsService.loadUserByUsername(nonExistentUsername);
    });

    assertThat(exception.getMessage()).contains(nonExistentUsername);

    verify(userRepository).findByUsername(nonExistentUsername);
  }
}
