package com.codeit.sb02mplteam2.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.sb02mplteam2.domain.admin.service.BasicAdminService;
import com.codeit.sb02mplteam2.domain.auth.dto.LoginRequest;
import com.codeit.sb02mplteam2.domain.setting.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.security.jwt.JwtBlacklist;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Security 통합 테스트")
//TODO 잠시 테스트 끔
@Disabled
public class SecurityIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AlarmSettingRepository alarmSettingRepository;

  @Autowired
  private BasicAdminService adminService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtBlacklist jwtBlacklist;

  private User normalUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    alarmSettingRepository.deleteAll();
    userRepository.deleteAll();

    normalUser = new User(
        "normalUser",
        "normal@example.com",
        passwordEncoder.encode("password"),
        null
    );
    userRepository.save(normalUser);

    adminUser = new User(
        "adminUser",
        "admin@example.com",
        passwordEncoder.encode("password"),
        null
    );
    adminUser.updateRole(Role.ADMIN);
    userRepository.save(adminUser);
  }

  @Test
  @DisplayName("로그인 성공: 올바른 정보로 로그인 시 200 OK와 토큰을 반환한다.")
  void login_success() throws Exception {
    // given
    LoginRequest loginRequest = new LoginRequest("normal@example.com", "password");

    // when & then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(cookie().exists(JwtService.REFRESH_TOKEN_COOKIE_NAME))
        .andDo(print());
  }

  @Test
  @DisplayName("로그인 실패: 잘못된 비밀번호로 로그인 시 401 Unauthorized를 반환한다.")
  void login_fail_with_wrong_password() throws Exception {
    // given
    LoginRequest loginRequest = new LoginRequest("normal@example.com", "wrongPassword");

    // when & then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("로그인 실패: 계정이 잠긴 사용자는 로그인 시 401 Unauthorized를 반환한다.")
  void login_fail_when_account_is_locked() throws Exception {
    // given 사용자를 강제로 잠금 상태로 만든다.
    adminService.lockUser(normalUser.getId());

    LoginRequest loginRequest = new LoginRequest("normal@example.com", "password");

    // when & then
    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andDo(print()); // LockedException 이 발생하고 FailureHandler가 401로 처리
  }

  @Test
  @DisplayName("인증 실패: 토큰 없이 보호된 API 접근 시 401 Unauthorized를 반환한다.")
  void access_protected_api_without_token() throws Exception {
    // when & then
    mockMvc.perform(get("/api/users/me"))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("인가 실패: User 권한으로 ADMIN 전용 API 접근 시 403 Forbidden을 반환한다.")
  void access_admin_api_with_user_role() throws Exception {
    // given
    String userAccessToken = getAccessToken("normal@example.com", "password");

    // when & then
    mockMvc.perform(post("/api/admin/users/{userId}/lock", normalUser.getId())
        .header("Authorization", "Bearer " + userAccessToken))
        .andExpect(status().isForbidden())
        .andDo(print());
  }

  private String getAccessToken(String email, String password) throws Exception {
    LoginRequest loginRequest = new LoginRequest(email, password);

    MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    String responseBody = result.getResponse().getContentAsString();
    // JwtLoginSuccessHandler에서 액세스 토큰을 JSON 문자열로 변환하여 응답 본문에 썼기 때문에,
    // 다시 String 객체로 파싱해야 합니다. (e.g., "\"token_string\"" -> "token_string")
    return objectMapper.readValue(responseBody, String.class);
  }
}
