package com.codeit.sb02mplteam2.domain.content.batch;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("movieBatchHealthIndicator")
@RequiredArgsConstructor
public class MovieBatchHealthIndicator implements HealthIndicator {

  private final BatchFailureTracker batchFailureTracker;

  private static final ContentCategory CATEGORY = ContentCategory.MOVIE;
  private static final long WINDOW_MINUTES = 30;

  @Override
  public Health health() {
    boolean failed = batchFailureTracker.isFailedRecently(CATEGORY, Duration.ofMinutes(WINDOW_MINUTES));
    Instant lastFailedAt = batchFailureTracker.getLastFailedTime(CATEGORY);

    Health.Builder builder = failed ? Health.down() : Health.up();

    builder.withDetail("category", CATEGORY.name())
        .withDetail("windowMinutes", WINDOW_MINUTES)
        .withDetail("lastFailedAt", lastFailedAt != null ? lastFailedAt.toString() : "NONE")
        .withDetail("message", failed ? "최근 30분 이내 실패 발생" : "최근 30분간 실패 없음");

    return builder.build();
  }
}