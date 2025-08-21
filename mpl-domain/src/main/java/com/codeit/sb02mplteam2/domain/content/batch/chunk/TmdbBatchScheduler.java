package com.codeit.sb02mplteam2.domain.content.batch.chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbBatchScheduler {

  private final JobLauncher jobLauncher;
  private final JobExplorer jobExplorer;
  private final @Qualifier("importTmdbJob") Job importTmdbJob;

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void runJob() {
    if (isRunning("importTmdbJob")) {
      log.warn("importTmdbJob 아직 실행 중, 트리거 건너뜀");
      return;
    }

    try {
      JobParameters params = new JobParametersBuilder()
          .addLong("runId", System.currentTimeMillis())
          .addLong("maxPages", 10L)
          .addLong("rateLimitMs", 250L)
          .toJobParameters();

      JobExecution exec = jobLauncher.run(importTmdbJob, params);

      if (exec.getStatus().isUnsuccessful()) {
        log.error("tmdbContentUpdateJob 실패. ExitStatus={}, ExitDescription={}",
            exec.getExitStatus().getExitCode(), exec.getExitStatus().getExitDescription());
      } else {
        log.info("tmdbContentUpdateJob 성공");
      }
    } catch (Exception e) {
      log.error("tmdbContentUpdateJob 런처 예외", e);
    }
  }

  private boolean isRunning(String jobName) {
    return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
  }
}