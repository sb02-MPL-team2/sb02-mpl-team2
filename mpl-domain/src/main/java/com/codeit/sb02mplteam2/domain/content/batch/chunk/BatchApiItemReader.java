package com.codeit.sb02mplteam2.domain.content.batch.chunk;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class BatchApiItemReader<T> implements ItemStreamReader<T> {

  @FunctionalInterface
  public interface PageFetcher<T> {
    List<T> fetchPage(int page) throws Exception;
  }

  private final PageFetcher<T> fetcher;
  private final String stateKey;

  private final Deque<T> buffer = new ArrayDeque<>();

  private int page = 1;
  private int maxPages = Integer.MAX_VALUE;
  private int rateLimitMs = 0;
  private boolean finished = false;

  public BatchApiItemReader(PageFetcher<T> fetcher, String stateKey) {
    if (fetcher == null) {
      throw new IllegalArgumentException("fetcher");
    }
    if (stateKey == null || stateKey.isBlank()) {
      throw new IllegalArgumentException("stateKey");
    }
    this.fetcher = fetcher;
    this.stateKey = stateKey;
  }

  public void setMaxPages(int maxPages) {
    if (maxPages < 1) {
      return;
    }
    this.maxPages = maxPages;
  }

  public void setRateLimitMs(int rateLimitMs) {
    if (rateLimitMs < 0) {
      return;
    }
    this.rateLimitMs = rateLimitMs;
  }

  // 호출
  @Override
  public void open(ExecutionContext context) {
    if (context != null) {
      int saved = context.getInt(stateKey, 1);
      if (saved > 1) {
        this.page = saved;
      } else {
        this.page = 1;
      }
    } else {
      this.page = 1;
    }
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
        finished = true;
        return null;
      }

      if (rateLimitMs > 0) {
        try {
          Thread.sleep(rateLimitMs);
        } catch (InterruptedException ignored) {
        }
      }

      List<T> pageItems = fetcher.fetchPage(page);
      if (pageItems == null || pageItems.isEmpty()) {
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
      context.putInt(stateKey, toSave);
    }
  }

  // 스텝 끝
  @Override
  public void close() {
    buffer.clear();
    finished = false;
  }
}