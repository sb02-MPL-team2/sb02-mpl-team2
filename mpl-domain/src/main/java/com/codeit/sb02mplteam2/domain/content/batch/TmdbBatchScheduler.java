package com.codeit.sb02mplteam2.domain.content.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbBatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job tmdbContentUpdateJob;

  @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
  public void runJob() {
    try {
      JobParameters params = new JobParametersBuilder()
          .addLong("timestamp", System.currentTimeMillis())
          .toJobParameters();
      JobExecution exec = jobLauncher.run(tmdbContentUpdateJob, params);

      if (exec.getStatus().isUnsuccessful()) {
        log.error("tmdbContentUpdateJob FAILED. ExitStatus={}, ExitDescription={}",
            exec.getExitStatus().getExitCode(), exec.getExitStatus().getExitDescription());
      } else {
        log.info("tmdbContentUpdateJob SUCCESS");
      }
    } catch (Exception e) {
      log.error("tmdbContentUpdateJob 런처 예외", e);
    }
  }
}