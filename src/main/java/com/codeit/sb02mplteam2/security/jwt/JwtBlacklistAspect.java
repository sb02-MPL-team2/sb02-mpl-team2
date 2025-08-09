package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class JwtBlacklistAspect {

  private final JwtBlacklist jwtBlacklist;

  @Before("@annotation(com.codeit.sb02mplteam2.security.jwt.CheckJwtBlacklist)")
  public void checkBlacklist() {
    log.debug("AOP: @CheckJwtBlacklist - 블랙리스트 검사를 시작합니다.");

    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    Optional<String> optionalAccessToken = resolveAccessToken(request);

    if(optionalAccessToken.isEmpty()) {
      throw new MplException(ErrorCode.UNAUTHORIZED);
    }

    String accessToken = optionalAccessToken.get();

    if (jwtBlacklist.contains(accessToken)) {
      log.warn("블랙리스트에 등록된 토큰으로 접근 시도: {}", accessToken);
      throw new MplException(ErrorCode.BLACKLIST_TOKEN);
    }
    log.debug("AOP: @CheckJwtBlacklist - 토큰이 유효합니다. API를 계속 진행합니다.");
  }

  private Optional<String> resolveAccessToken(HttpServletRequest request) {
    String prefix = "Bearer ";
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
        .filter(header -> header.startsWith(prefix))
        .map(header -> header.substring(prefix.length()));
  }
}
