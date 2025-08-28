package com.codeit.sb02mplteam2;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class Initializer implements ApplicationRunner {
  private final JobLauncher jobLauncher;
  private final Job importTmdbJob;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("애플리케이션 시작 시 TMDB 배치 잡을 자동으로 실행");

    // JobParameters 설정 (컨트롤러의 파라미터와 동일하게 설정 가능)
    JobParameters params = new JobParametersBuilder()
        .addLong("runId", System.currentTimeMillis()) // Job은 동일한 파라미터로 재실행되지 않으므로,毎回違う値を 주는 것이 중요합니다.
        .addString("category", "ALL")
        .addLong("maxPages", 10L)
        .addLong("rateLimitMs", 300L)
        .toJobParameters();

    try {
      jobLauncher.run(importTmdbJob, params);
    } catch (Exception e) {
      log.error("!!! 배치 잡 자동 실행 중 오류 발생: {}", e.getMessage());
    }
  }
}
