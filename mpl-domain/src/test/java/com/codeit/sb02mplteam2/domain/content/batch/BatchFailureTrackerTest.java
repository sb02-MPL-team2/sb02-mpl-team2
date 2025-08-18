package com.codeit.sb02mplteam2.domain.content.batch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatchFailureTrackerTest {

  private BatchFailureTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new BatchFailureTracker();
  }

  @Test
  void markFailed_and_getLastFailedTime() {
    tracker.markFailed(ContentCategory.MOVIE);
    assertNotNull(tracker.getLastFailedTime(ContentCategory.MOVIE));
  }

  @Test
  void markFailed_doesNotAffectOtherCategories() {
    tracker.markFailed(ContentCategory.MOVIE);

    assertNull(tracker.getLastFailedTime(ContentCategory.TV));
    assertFalse(tracker.isFailedRecently(ContentCategory.TV, Duration.ofMinutes(30)));
  }

  @Test
  void isFailedRecently_true_whenWithinDuration() {
    tracker.markFailed(ContentCategory.MOVIE);

    boolean result = tracker.isFailedRecently(ContentCategory.MOVIE, Duration.ofMinutes(10));
    assertTrue(result);
  }

  @Test
  void isFailedRecently_false_whenTooOld() {
    Instant old = Instant.now().minus(Duration.ofHours(1));
    tracker.forceSetFailureTime(ContentCategory.MOVIE, old);

    boolean result = tracker.isFailedRecently(ContentCategory.MOVIE, Duration.ofMinutes(30));
    assertFalse(result);
  }
}
