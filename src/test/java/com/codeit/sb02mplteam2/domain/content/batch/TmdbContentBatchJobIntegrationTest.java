package com.codeit.sb02mplteam2.domain.content.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.codeit.sb02mplteam2.config.BatchConfig;
import com.codeit.sb02mplteam2.domain.auth.service.BasicAuthService;
import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateMoviesTasklet;
import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateTvsTasklet;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.batch.job.enabled=false",
    "spring.sql.init.mode=never",
    "spring.jpa.hibernate.ddl-auto=none",

    "spring.batch.jdbc.initialize-schema=always",
    "spring.batch.jdbc.schema=classpath:org/springframework/batch/core/schema-h2.sql",
    "spring.batch.jdbc.platform=h2",

    "spring.datasource.url=jdbc:h2:mem:batchtest;DB_CLOSE_DELAY=-1",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
@Import({
    BatchConfig.class,
    TmdbContentBatchJobConfig.class,
    UpdateMoviesTasklet.class,
    UpdateTvsTasklet.class,
    BatchSchemaInitConfig.class
})
class TmdbContentBatchJobIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job tmdbContentUpdateJob;

  @MockitoBean
  private BasicContentService basicContentService;

  @MockitoBean
  private BasicAuthService basicAuthService;

  @MockitoBean
  private TmdbService tmdbService;

  private JobParameters uniqueJobParameters() {
    return new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
  }

  @Test
  void job_runsSuccessfully() throws Exception {
    JobExecution execution = jobLauncher.run(tmdbContentUpdateJob, uniqueJobParameters());

    assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

    verify(basicContentService, times(1)).saveTmdbMovies(ContentCategory.MOVIE);
    verify(basicContentService, times(1)).saveTmdbTvs(ContentCategory.TV);
    verifyNoMoreInteractions(basicContentService);
  }

  @Test
  void job_fails_when_tv_service_throws_exception() throws Exception {
    doThrow(new RuntimeException("TV 저장 실패"))
        .when(basicContentService)
        .saveTmdbTvs(ContentCategory.TV);

    JobExecution execution = jobLauncher.run(tmdbContentUpdateJob, uniqueJobParameters());

    assertThat(execution.getStatus()).isEqualTo(BatchStatus.FAILED);
    verify(basicContentService, atLeastOnce()).saveTmdbMovies(ContentCategory.MOVIE);
  }
}