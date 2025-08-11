package com.codeit.sb02mplteam2.domain.content.batch;

import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TmdbBatchMetrics {

  private final MeterRegistry meterRegistry;
  private final Map<ContentCategory, AtomicInteger> gaugeMap = new ConcurrentHashMap<>();

  public void incrementSuccessCount(ContentCategory category) {
    meterRegistry.counter("batch.tmdb.success", "category", category.getMetricKey()).increment();
  }

  public void incrementFailCount(ContentCategory category) {
    meterRegistry.counter("batch.tmdb.fail", "category", category.getMetricKey()).increment();
  }

  public void recordItemCount(ContentCategory category, int count) {
    gaugeMap.computeIfAbsent(category, key -> {
      AtomicInteger gaugeValue = new AtomicInteger();
      meterRegistry.gauge("batch.tmdb.item.count",
          List.of(Tag.of("category", key.getMetricKey())),
          gaugeValue);
      return gaugeValue;
    }).set(count);
  }
}
