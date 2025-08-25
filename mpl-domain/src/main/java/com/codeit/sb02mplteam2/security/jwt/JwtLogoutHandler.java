package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.event.LogoutToSseEvent;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  private final JwtService jwtService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    resolveRefreshToken(request)
        .ifPresent(refreshToken -> {
          jwtService.invalidateJwtSession(refreshToken);
          invalidateRefreshTokenCookie(response);
        });

    if(authentication != null &&
        authentication.getPrincipal() instanceof MplUserDetails userDetails) {
      eventPublisher.publishEvent(new LogoutToSseEvent(this, userDetails.getId()));
    }

  }

  private Optional<String> resolveRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return Optional.empty();
    }
    return Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(JwtService.REFRESH_TOKEN_COOKIE_NAME))
        .findFirst()
        .map(Cookie::getValue);
  }

  private void invalidateRefreshTokenCookie(HttpServletResponse response) {
    Cookie refreshTokenCookie = new Cookie(JwtService.REFRESH_TOKEN_COOKIE_NAME, "");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setMaxAge(0);
    response.addCookie(refreshTokenCookie);
  }
}
