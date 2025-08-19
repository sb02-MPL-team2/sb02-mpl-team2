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

import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateTvsTasklet;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

class UpdateTvsTaskletTest {

  private BasicContentService basicContentService;
  private TmdbBatchMetrics tmdbBatchMetrics;
  private BatchFailureTracker batchFailureTracker;
  private UpdateTvsTasklet tasklet;

  @BeforeEach
  void setUp() {
    basicContentService = mock(BasicContentService.class);
    tmdbBatchMetrics = mock(TmdbBatchMetrics.class);
    batchFailureTracker = new BatchFailureTracker();

    tasklet = new UpdateTvsTasklet(
        basicContentService,
        tmdbBatchMetrics,
        batchFailureTracker
    );
  }

  @Test
  void tv_success_metrics() throws Exception {
    var status = tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));

    verify(basicContentService).saveTmdbTvs(ContentCategory.TV);
    verify(tmdbBatchMetrics).incrementSuccessCount(ContentCategory.TV);
    verify(tmdbBatchMetrics, never()).incrementFailCount(any());
    assertEquals(RepeatStatus.FINISHED, status);
  }

  @Test
  void tv_failure_metrics() {
    doThrow(new RuntimeException("fail")).when(basicContentService).saveTmdbTvs(ContentCategory.TV);

    Exception ex = assertThrows(Exception.class, () ->
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class))
    );

    assertEquals("fail", ex.getMessage());
    verify(tmdbBatchMetrics).incrementFailCount(ContentCategory.TV);
    assertNotNull(batchFailureTracker.getLastFailedTime(ContentCategory.TV));
  }

  @Test
  void tv_failure_callsMetricsAndTrackerInOrder() {
    doThrow(new RuntimeException("fail")).when(basicContentService).saveTmdbTvs(ContentCategory.TV);

    assertThrows(Exception.class, () ->
        tasklet.execute(mock(StepContribution.class), mock(ChunkContext.class))
    );

    InOrder inOrder = inOrder(tmdbBatchMetrics);
    inOrder.verify(tmdbBatchMetrics).incrementFailCount(ContentCategory.TV);
  }
}