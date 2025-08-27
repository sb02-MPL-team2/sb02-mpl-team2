//package com.codeit.sb02mplteam2.domain.content.batch;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.atLeastOnce;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoInteractions;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.when;
//
//import com.codeit.sb02mplteam2.config.BatchConfig;
//import com.codeit.sb02mplteam2.domain.auth.service.BasicAuthService;
//import com.codeit.sb02mplteam2.domain.content.batch.tasklet.TmdbContentBatchJobConfig;
//import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateMoviesTasklet;
//import com.codeit.sb02mplteam2.domain.content.batch.tasklet.UpdateTvsTasklet;
//import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
//import com.codeit.sb02mplteam2.domain.content.service.BasicContentService;
//import com.codeit.sb02mplteam2.domain.content.service.TmdbService;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//@SpringBootTest
//@Disabled
////TODO 잠시 테스트 끔
//@ActiveProfiles("test")
//@TestPropertySource(properties = {
//    "spring.batch.job.enabled=false",
//    "spring.sql.init.mode=never",
//    "spring.jpa.hibernate.ddl-auto=none",
//
//    "spring.batch.jdbc.initialize-schema=always",
//    "spring.batch.jdbc.schema=classpath:org/springframework/batch/core/schema-h2.sql",
//    "spring.batch.jdbc.platform=h2",
//
//    "spring.datasource.url=jdbc:h2:mem:batchtest;DB_CLOSE_DELAY=-1",
//    "spring.datasource.driverClassName=org.h2.Driver",
//    "spring.datasource.username=sa",
//    "spring.datasource.password="
//})
//@Import({
//    BatchConfig.class,
//    TmdbContentBatchJobConfig.class,
//    UpdateMoviesTasklet.class,
//    UpdateTvsTasklet.class,
//    BatchSchemaInitConfig.class
//})
//class TmdbContentBatchJobIntegrationTest {
//
//  @Autowired
//  private JobLauncher jobLauncher;
//
//  @Autowired
//  private Job tmdbContentUpdateJob;
//
//  @MockitoBean
//  private BasicContentService basicContentService;
//
//  @MockitoBean
//  private BasicAuthService basicAuthService;
//
//  @MockitoBean
//  private TmdbService tmdbService;
//
//  @MockitoBean
//  private com.codeit.sb02mplteam2.security.jwt.JwtService jwtService;
//
//  @MockitoBean
//  private com.codeit.sb02mplteam2.Initializer initializer;
//
//  private JobParameters uniqueJobParameters() {
//    return new JobParametersBuilder()
//        .addLong("time", System.currentTimeMillis())
//        .toJobParameters();
//  }
//
//  @Test
//  void job_runsSuccessfully() throws Exception {
//    when(basicContentService.saveTmdbMovies(ContentCategory.MOVIE)).thenReturn(3);
//    when(basicContentService.saveTmdbTvs(ContentCategory.TV)).thenReturn(5);
//
//    JobExecution execution = jobLauncher.run(tmdbContentUpdateJob, uniqueJobParameters());
//
//    assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
//
//    verify(basicContentService, times(1)).saveTmdbMovies(ContentCategory.MOVIE);
//    verify(basicContentService, times(1)).saveTmdbTvs(ContentCategory.TV);
//    verifyNoMoreInteractions(basicContentService);
//
//    verifyNoInteractions(tmdbService);
//  }
//
//  @Test
//  void job_fails_when_tv_service_throws_exception() throws Exception {
//    when(basicContentService.saveTmdbMovies(ContentCategory.MOVIE)).thenReturn(2);
//    doThrow(new RuntimeException("TV 저장 실패"))
//        .when(basicContentService)
//        .saveTmdbTvs(ContentCategory.TV);
//
//    JobExecution execution = jobLauncher.run(tmdbContentUpdateJob, uniqueJobParameters());
//
//    assertThat(execution.getStatus()).isEqualTo(BatchStatus.FAILED);
//    verify(basicContentService, atLeastOnce()).saveTmdbMovies(ContentCategory.MOVIE);
//    verify(basicContentService, times(1)).saveTmdbTvs(ContentCategory.TV);
//  }
//}