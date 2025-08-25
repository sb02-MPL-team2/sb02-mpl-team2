package com.codeit.sb02mplteam2.domain.content.batch.monitoring;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class BatchFailureTracker {

  private final Map<ContentCategory, Instant> lastFailedTimes = new ConcurrentHashMap<>();

  public void markFailed(ContentCategory category) {
    lastFailedTimes.put(category, Instant.now());
  }

  public boolean isFailedRecently(ContentCategory category, Duration within) {
    Instant lastFail = lastFailedTimes.get(category);
    return lastFail != null && lastFail.isAfter(Instant.now().minus(within));
  }

  public Instant getLastFailedTime(ContentCategory category) {
    return lastFailedTimes.get(category);
  }

  // 테스트 전용: 강제로 실패 시간 주입
  public void forceSetFailureTime(ContentCategory category, Instant time) {
    lastFailedTimes.put(category, time);
  }
}