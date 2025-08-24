package com.codeit.sb02mplteam2.domain.livewatch.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public enum RedisTTLStrategy {

  PARTICIPANT_SESSION(Duration.ofHours(5), "참가자 세션 정보");

  private final Duration duration;
  private final String description;

  RedisTTLStrategy(Duration duration, String description) {
    this.duration = duration;
    this.description = description;
  }

  public Duration getDuration() {
    return duration;
  }

  public String getDescription() {
    return description;
  }

  public long getSeconds() {
    return duration.getSeconds();
  }
}