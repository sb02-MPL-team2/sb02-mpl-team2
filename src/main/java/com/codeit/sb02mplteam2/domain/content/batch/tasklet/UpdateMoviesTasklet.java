package com.codeit.sb02mplteam2.domain.content.batch.tasklet;

import com.codeit.sb02mplteam2.domain.content.batch.BatchFailureTracker;
import com.codeit.sb02mplteam2.domain.content.batch.TmdbBatchMetrics;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateMoviesTasklet implements Tasklet {

  private final BasicContentService basicContentService;
  private final TmdbBatchMetrics tmdbBatchMetrics;
  private final BatchFailureTracker batchFailureTracker;
  private static final ContentCategory CATEGORY = ContentCategory.MOVIE;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    try {
      log.info("[Tasklet 시작] TMDB 영화 데이터 저장");
      int saved = basicContentService.saveTmdbMovies(CATEGORY);
      tmdbBatchMetrics.incrementSuccessCount(CATEGORY);
    } catch (Exception e) {
      tmdbBatchMetrics.incrementFailCount(CATEGORY);
      batchFailureTracker.markFailed(CATEGORY);
      log.error("[Tasklet 실패] TMDB 영화 데이터 저장 실패", e);
      throw e;
    }
    return RepeatStatus.FINISHED;
  }
}