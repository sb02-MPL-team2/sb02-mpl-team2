package com.codeit.sb02mplteam2.security.jwt;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import java.time.Instant;

public record JwtObject(
    Instant issueTime, // 토큰 발행 시간
    Instant expirationTime, // 토큰 만료 시간
    UserDto userDto,
    String token // JWT 문자열
)
{
  public boolean isExpired() {
    return expirationTime.isBefore(Instant.now());
  }
}
