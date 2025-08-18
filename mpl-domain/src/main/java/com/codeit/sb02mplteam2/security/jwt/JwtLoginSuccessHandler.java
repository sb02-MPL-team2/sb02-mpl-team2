package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Slf4j
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    MplUserDetails principal = (MplUserDetails) authentication.getPrincipal();
    log.info("로그인 성공. 사용자: {}", principal.getUserDto().email());

    log.debug("기존 JWT 세션 무효화 처리 시작. 사용자: {}", principal.getUserDto().email());
    jwtService.invalidateJwtSession(principal.getId());
    JwtSession jwtSession = jwtService.registerJwtSession(principal.getUserDto());
    log.debug("새로운 JWT 세션 등록 완료. AccessToken: {}", jwtSession.getAccessToken());

    String refreshToken = jwtSession.getRefreshToken();
    Cookie refreshTokenCookie = new Cookie(JwtService.REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshTokenCookie.setHttpOnly(true);
    response.addCookie(refreshTokenCookie);

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(jwtSession.getAccessToken()));
  }
}
