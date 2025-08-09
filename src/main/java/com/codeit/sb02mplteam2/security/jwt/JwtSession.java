package com.codeit.sb02mplteam2.security.jwt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jwt_sessions")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class JwtSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(columnDefinition = "BIGINT", updatable = false, nullable = false)
  private Long userId;

  @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
  private String accessToken;

  @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
  private String refreshToken;

  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private Instant expirationTime; // refreshToken 만료 시간, 만료 되면 Session 삭제

  @CreatedDate
  @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(columnDefinition = "timestamp with time zone")
  private Instant updatedAt;

  public JwtSession(Long userId, String accessToken, String refreshToken, Instant expirationTime) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expirationTime = expirationTime;
  }

  public boolean isExpired() {
    return expirationTime.isBefore(Instant.now());
  }

  public void update(String accessToken, String refreshToken, Instant expirationTime) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expirationTime = expirationTime;
  }



}
