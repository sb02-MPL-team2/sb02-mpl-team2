package com.codeit.sb02mplteam2.domain.content.batch.chunk;

import com.codeit.sb02mplteam2.domain.content.entity.BatchWatermark;
import com.codeit.sb02mplteam2.domain.content.repository.BatchWatermarkRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Slf4j
public class BatchApiItemReader<T> implements ItemStreamReader<T> {

  @FunctionalInterface
  public interface PageFetcher<T> {
    List<T> fetchPage(int page, LocalDate targetDate) throws Exception;
  }

  private final PageFetcher<T> fetcher;
  private final String taskKey;
  private final BatchWatermarkRepository watermarkRepository;

  private final Deque<T> buffer = new ArrayDeque<>();

  private int page = 1;
  private int maxPages = Integer.MAX_VALUE;
  private int rateLimitMs = 0;
  private boolean finished = false;

  private LocalDate targetDate;

  public BatchApiItemReader(PageFetcher<T> fetcher,
      String taskKey,
      BatchWatermarkRepository watermarkRepository) {
    if (fetcher == null) throw new IllegalArgumentException("fetcher");
    if (taskKey == null || taskKey.isBlank()) throw new IllegalArgumentException("taskKey");
    this.fetcher = fetcher;
    this.taskKey = taskKey;
    this.watermarkRepository = watermarkRepository;
  }

  public void setMaxPages(int maxPages) {
    if (maxPages > 0) this.maxPages = maxPages;
  }

  public void setRateLimitMs(int rateLimitMs) {
    if (rateLimitMs >= 0) this.rateLimitMs = rateLimitMs;
  }

  // 호출
  @Override
  public void open(ExecutionContext context) {
    // 워터마크 조회
    BatchWatermark wm = watermarkRepository.findById(taskKey).orElse(null);

    if (wm == null) {
      targetDate = LocalDate.now().minusDays(2);
      watermarkRepository.save(new BatchWatermark(taskKey, targetDate));
      log.info("[reader:{}] 워터마크 없음 → targetDate={}", taskKey, targetDate);
    } else {
      targetDate = wm.getLastProcessedDate().plusDays(1);
      log.info("[reader:{}] 워터마크 존재 → lastDate={}, targetDate={}",
          taskKey, wm.getLastProcessedDate(), targetDate);
    }

    this.page = 1;
    this.finished = false;
    this.buffer.clear();
  }

  @Override
  public T read() throws Exception {
    if (finished) {
      return null;
    }

    if (buffer.isEmpty()) {
      if (page > maxPages) {
        log.info("[reader:{}] maxPages={} 초과 → 종료", taskKey, maxPages);
        finished = true;
        return null;
      }

      if (rateLimitMs > 0) {
        try {
          Thread.sleep(rateLimitMs);
        } catch (InterruptedException ignored) {
        }
      }

      List<T> pageItems = fetcher.fetchPage(page, targetDate);
      if (pageItems == null || pageItems.isEmpty()) {
        log.info("[reader:{}] page={} (date={}) → 빈 리스트 반환", taskKey, page, targetDate);
        finished = true;
        return null;
      }

      buffer.addAll(pageItems);
      page++;
    }

    return buffer.pollFirst();
  }

  // 체크포인트
  @Override
  public void update(ExecutionContext context) {
    if (context != null) {
      int toSave;
      if (buffer.isEmpty()) {
        toSave = page;
      } else {
        toSave = page - 1;
      }
      context.putInt(taskKey, toSave);
      log.debug("[reader:{}] 체크포인트 저장 page={}", taskKey, toSave);
    }
  }

  // 스텝 끝
  @Override
  public void close() {
    buffer.clear();
    finished = false;

    // 스텝 끝나면 워터마크 갱신
    watermarkRepository.findById(taskKey).ifPresent(wm -> {
      wm.updateLastProcessedDate(targetDate);
      watermarkRepository.save(wm);
      log.info("[reader:{}] 워터마크 갱신 완료 → lastProcessedDate={}", taskKey, targetDate);
    });
  }
}