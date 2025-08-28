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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtService jwtService;
  
  @Value("${mpl.oauth.frontend-base-url}")
  private String frontendBaseUrl;
  
  @Value("${mpl.oauth.callback-success-path}")
  private String callbackSuccessPath;
  
  @Value("${mpl.oauth.cookie-domain}")
  private String cookieDomain;

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
    refreshTokenCookie.setPath("/");  // 모든 경로에서 접근 가능
    
    // Docker 환경 대응 쿠키 도메인 설정
    if (cookieDomain != null && !cookieDomain.isBlank()) {
      refreshTokenCookie.setDomain(cookieDomain);
      log.debug("쿠키 도메인 설정: {}", cookieDomain);
    }
    
    response.addCookie(refreshTokenCookie);

    // OAuth2 vs 일반 로그인 구분 처리
    String requestURI = request.getRequestURI();
    if (requestURI != null && requestURI.contains("/oauth2/") || 
        authentication.getClass().getSimpleName().contains("OAuth2")) {
      
      // OAuth2 로그인인 경우 프론트엔드 콜백 페이지로 리다이렉트
      log.info("OAuth2 로그인 감지 - 콜백 페이지로 리다이렉트");
      
      // Docker 환경 디버깅 정보 출력
      log.info("=== Docker OAuth2 쿠키 디버깅 ===");
      log.info("Request Host: {}", request.getHeader("Host"));
      log.info("Request X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
      log.info("Request X-Forwarded-Port: {}", request.getHeader("X-Forwarded-Port"));
      log.info("서버명: {}, 포트: {}", request.getServerName(), request.getServerPort());
      log.info("쿠키 도메인: {}", cookieDomain);
      log.info("쿠키명: {}", JwtService.REFRESH_TOKEN_COOKIE_NAME);
      log.info("쿠키값 설정됨: {}", refreshToken != null ? "YES" : "NO");
      
      String redirectUrl = frontendBaseUrl + callbackSuccessPath;
      log.info("리다이렉트 URL: {}", redirectUrl);
      log.info("==============================");
      
      response.sendRedirect(redirectUrl);
      
    } else {
      
      // 일반 로그인인 경우 JSON 응답 반환 (기존 방식)
      log.info("일반 로그인 - JSON 응답 반환");
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(objectMapper.writeValueAsString(jwtSession.getAccessToken()));
    }
  }
}
