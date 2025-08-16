package com.codeit.sb02mplteam2.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.auth.entity.PasswordResetToken;
import com.codeit.sb02mplteam2.domain.auth.repository.PasswordResetTokenRepository;
import com.codeit.sb02mplteam2.domain.auth.service.BasicAuthService;
import com.codeit.sb02mplteam2.domain.mail.service.EmailService;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicAuthService 단위 테스트")
public class BasicAuthServiceTest {

  @InjectMocks
  private BasicAuthService authService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private EmailService emailService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User(1L, "testUser", "test@example.com", "password", false, false,
        0, 0, null, null, Role.USER, null, null);

//    @Value 로 주입되는 필드 값을 테스트 환경에서 수동 설정
    ReflectionTestUtils.setField(authService, "resetPasswordUrl",
        "http://localhost/reset-password?token=");
  }

  @Nested
  @DisplayName("비밀번호 재설정 토큰 생성 (createPasswordResetTokenForUser)")
  class CreatePasswordResetToken {

    @Test
    @DisplayName("성공 - 정상적인 이메일로 요청 시 토큰을 생성하고 이메일을 발송한다")
    void createToken_Success() {
      // given
      String userEmail = "test@example.com";
      given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(testUser));

      // when
      authService.createPasswordResetTokenForUser(userEmail);

      // then
      // 기존 토큰이 있는지 확인 (없으므로 delete는 호출 안됨)
      verify(passwordResetTokenRepository, times(1)).findByUserId(testUser.getId());
      verify(passwordResetTokenRepository, never()).delete(any(PasswordResetToken.class));

      // 새로운 토큰을 저장하는지 확인
      ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
      verify(passwordResetTokenRepository, times(1)).save(tokenCaptor.capture());
      PasswordResetToken savedToken = tokenCaptor.getValue();
      assertThat(savedToken.getUser()).isEqualTo(testUser);
      assertThat(savedToken.getToken()).isNotNull();

      // 이메일 발송을 요청하는지 확인
      ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
      verify(emailService, times(1)).sendEmail(anyString(), anyString(), linkCaptor.capture());
      assertThat(linkCaptor.getValue()).contains(savedToken.getToken());
    }

    @Test
    @DisplayName("실패 - 존재하지 않는 이메일로 요청 시 USER_NOT_FOUND 예외 발생")
    void createToken_Fail_UserNotFound() {
      // given
      String nonExistentEmail = "nonfound@example.com";
      given(userRepository.findByEmail(nonExistentEmail)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> authService.createPasswordResetTokenForUser(nonExistentEmail))
          .isInstanceOf(MplException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

      verify(passwordResetTokenRepository, never()).save(any());
      verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("새 비밀번호로 재설정 (resetPassword")
  class ResetPassword {

    private String tokenValue;
    private PasswordResetToken validToken;

    @BeforeEach
    void resetPasswordSetUp() {
      tokenValue = UUID.randomUUID().toString();
      validToken = new PasswordResetToken(tokenValue, testUser);
    }

    @Test
    @DisplayName("성공 - 유효한 토큰으로 요청 시 비밀번호를 암호화하여 변경하고 토큰을 삭제한다.")
    void resetPassword_Success() {
      // given
      String newPassword = "newSecurePassword123!";
      String encodedPassword = "encodedNewPassword";
      given(passwordResetTokenRepository.findByToken(tokenValue)).willReturn(Optional.of(validToken));
      given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

      // when
      authService.resetPassword(tokenValue, newPassword);

      //then
      assertThat(testUser.getPassword()).isEqualTo(encodedPassword);
      verify(passwordResetTokenRepository, times(1)).delete(validToken);
    }

    @Test
    @DisplayName("실패 - 조재하지 않는 토큰으로 요청 시 INVALID_TOKEN 예외 발생한다.")
    void resetPassword_Fail_InvalidToken() {
      // given
      String invalidToken = "invalid-token-value";
      given(passwordResetTokenRepository.findByToken(invalidToken)).willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> authService.resetPassword(invalidToken, "newPassword"))
          .isInstanceOf(MplException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);

      verify(passwordResetTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("실패 - 만료된 토큰으로 요청 시 EXPIRED_TOKEN 예외 발생한다.")
    void resetPassword_Fail_ExpiredToken() {
      // given
      ReflectionTestUtils.setField(validToken, "expiryDate", LocalDateTime.now().minusMinutes(10));
      given(passwordResetTokenRepository.findByToken(tokenValue)).willReturn(Optional.of(validToken));

      // when & then
      assertThatThrownBy(() -> authService.resetPassword(tokenValue, "newPassword"))
          .isInstanceOf(MplException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXPIRED_TOKEN);

      verify(passwordResetTokenRepository, times(1)).delete(validToken);
    }

  }

}
