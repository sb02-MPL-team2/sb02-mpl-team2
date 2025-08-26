package com.codeit.sb02mplteam2.domain.content.batch.chunk.tmdb;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component("tmdbContentWriter")
@RequiredArgsConstructor
public class TmdbContentJdbcUpsertWriter implements ItemWriter<ContentRow> {

  private final JdbcBatchItemWriter<ContentRow> delegate;

  @Override
  public void write(Chunk<? extends ContentRow> chunk) throws Exception {
    if (chunk == null || chunk.isEmpty()) {
      log.debug("[tmdb] 비어 있는 청크라 저장 건너뜀");
      return;
    }

    final int count = chunk.size();
    final long startNs = System.nanoTime();

    try {
      delegate.write(chunk);
      final long tookMs = (System.nanoTime() - startNs) / 1_000_000;

      log.info("[tmdb] {}건 수집 및 upsert 완료 ({} ms)", count, tookMs);

    } catch (Exception e) {
      log.error("[tmdb] {}건 저장 중 오류 발생", count, e);
      throw e;
    }
  }
}