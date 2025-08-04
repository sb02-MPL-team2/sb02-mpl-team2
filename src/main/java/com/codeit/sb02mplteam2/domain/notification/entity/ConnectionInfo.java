package com.codeit.sb02mplteam2.domain.notification.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
public class ConnectionInfo {
  private final Long userId;
  private final LocalDateTime connectedAt;
  private final SseEmitter sseEmitter;
  private LocalDateTime lastActiveAt;

  public ConnectionInfo(Long userId, SseEmitter sseEmitter) {
    this.userId = userId;
    this.connectedAt = LocalDateTime.now();
    this.sseEmitter = sseEmitter;
    this.lastActiveAt = LocalDateTime.now();
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = LocalDateTime.now();
  }
}
