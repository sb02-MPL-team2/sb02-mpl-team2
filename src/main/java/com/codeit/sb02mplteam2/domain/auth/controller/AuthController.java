package com.codeit.sb02mplteam2.domain.auth.controller;

import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.service.UserService;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.security.jwt.JwtService;
import com.codeit.sb02mplteam2.security.jwt.JwtSession;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController implements AuthApi{

  private final UserService userService;
  private final JwtService jwtService;

  @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<UserDto> signup(
      @RequestPart("userCreateRequest") @Valid UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    UserDto userDto = userService.create(userCreateRequest, Optional.ofNullable(profile));
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PostMapping("/auth/refresh")
  public ResponseEntity<String> refresh(
      @CookieValue(name = JwtService.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
      HttpServletResponse response
  ) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new MplException(ErrorCode.INVALID_TOKEN, Map.of("refreshToken", refreshToken));
    }

    // Refresh Token 으로 새로운 세션 발급
    JwtSession newJwtSession = jwtService.refreshJwtSession(refreshToken);

    // 새로운 RefreshToken을 HttpOnly 쿠키에 설정
    Cookie newRefreshTokenCookie = new Cookie(JwtService.REFRESH_TOKEN_COOKIE_NAME,
        newJwtSession.getRefreshToken());
    response.addCookie(newRefreshTokenCookie);

    return ResponseEntity.ok(newJwtSession.getAccessToken());
  }

}
