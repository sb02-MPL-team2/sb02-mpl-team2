package com.codeit.sb02mplteam2.security.jwt;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JwtSessionRepository extends JpaRepository<JwtSession, Long> {

  Optional<JwtSession> findByRefreshToken(String refreshToken);

  Optional<JwtSession> findByUserId(Long userId);

  List<JwtSession> findAllByExpirationTimeAfter(Instant after);

  boolean existsByAccessToken(String accessToken);

  boolean existsByRefreshToken(String refreshToken);
}
