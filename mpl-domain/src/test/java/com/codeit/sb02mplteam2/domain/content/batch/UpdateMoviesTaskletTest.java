package com.codeit.sb02mplteam2.domain.content.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateMoviesTasklet;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

class UpdateMoviesTaskletTest {

  private BasicContentService basicContentService;
  private TmdbBatchMetrics tmdbBatchMetrics;
  private BatchFailureTracker batchFailureTracker;
  private UpdateMoviesTasklet tasklet;

  @BeforeEach
  void setUp() {
    basicContentService = mock(BasicContentService.class);
    tmdbBatchMetrics = mock(TmdbBatchMetrics.class);
    batchFailureTracker = new BatchFailureTracker();

    tasklet = new UpdateMoviesTasklet(
        basicContentService,
        tmdbBatchMetrics,
        batchFailureTracker
    );
  }

  @Test
  void movie_success_metrics() throws Exception {
    var status = tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

    verify(basicContentService).saveTmdbMovies(ContentCategory.MOVIE);
    verify(tmdbBatchMetrics).incrementSuccessCount(ContentCategory.MOVIE);
    verify(tmdbBatchMetrics, never()).incrementFailCount(any());
    assertEquals(RepeatStatus.FINISHED, status);
  }

  @Test
  void movie_failure_metrics() {
    doThrow(new RuntimeException("test")).when(basicContentService).saveTmdbMovies(ContentCategory.MOVIE);

    Exception ex = assertThrows(Exception.class, () ->
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class))
    );

    assertEquals("test", ex.getMessage());
    verify(tmdbBatchMetrics).incrementFailCount(ContentCategory.MOVIE);
    assertNotNull(batchFailureTracker.getLastFailedTime(ContentCategory.MOVIE));
  }

  @Test
  void movie_failure_callsMetricsAndTrackerInOrder() {
    doThrow(new RuntimeException("fail")).when(basicContentService).saveTmdbMovies(ContentCategory.MOVIE);

    assertThrows(Exception.class, () ->
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class))
    );

    InOrder inOrder = inOrder(tmdbBatchMetrics);
    inOrder.verify(tmdbBatchMetrics).incrementFailCount(ContentCategory.MOVIE);
  }
}
