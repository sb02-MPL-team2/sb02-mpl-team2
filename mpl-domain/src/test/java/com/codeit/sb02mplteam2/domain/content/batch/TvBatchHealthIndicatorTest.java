package com.codeit.sb02mplteam2.domain.content.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.codeit.sb02mplteam2.domain.content.batch.monitoring.BatchFailureTracker;
import com.codeit.sb02mplteam2.domain.content.batch.monitoring.TvBatchHealthIndicator;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

class TvBatchHealthIndicatorTest {

  private BatchFailureTracker tracker;
  private TvBatchHealthIndicator indicator;

  @BeforeEach
  void setUp() {
    tracker = new BatchFailureTracker();
    indicator = new TvBatchHealthIndicator(tracker);
  }

  @Test
  void health_returnsUp_whenNoRecentFailure() {
    var health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
  }

  @Test
  void health_returnsDown_whenFailedRecently() {
    tracker.markFailed(ContentCategory.TV);

    var health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());

    var details = health.getDetails();
    assertNotNull(details.get("lastFailedAt"));
    assertEquals("최근 30분 이내 실패 발생", details.get("message"));
  }

  @Test
  void health_returnsUp_whenFailureIsTooOld() {
    tracker.forceSetFailureTime(ContentCategory.TV, Instant.now().minus(Duration.ofHours(2)));

    var health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
  }
}
