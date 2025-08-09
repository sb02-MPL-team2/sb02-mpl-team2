package com.codeit.sb02mplteam2.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BatchScheduler {

  private final JobLauncher jobLauncher;
  private final Job recommendJob;
  private final Job playlistHistoryJob;

  public BatchScheduler(JobLauncher jobLauncher,
      @Qualifier("RecommendJob") Job recommendJob,
      @Qualifier("PlaylistHistoryJob") Job playlistHistoryJob) {
    this.jobLauncher = jobLauncher;
    this.recommendJob = recommendJob;
    this.playlistHistoryJob = playlistHistoryJob;
  }

  @Scheduled(cron = "0 1 0 * * *")
  public void runRecommendJob() {
    LocalDateTime endDate = LocalDate.now().atStartOfDay();
    LocalDateTime startDate = endDate.minusDays(7);

    JobParameters jobParameters = new JobParametersBuilder()
        .addLocalDateTime("start", startDate)
        .addLocalDateTime("end", endDate)
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    try {
      log.info("추천 알고리즘 배치 시작");
      JobExecution execution = jobLauncher.run(recommendJob, jobParameters);
      log.info("추천 알고리즘 배치 상태 {}", execution.getStatus());
    } catch (Exception e) {
      log.error("추천 알고리즘 배치 실패 {}",e.getMessage());
    }
  }

  @Scheduled(cron = "0 0 6 * * *")
  public void runPlaylistJob() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    try {
      log.info("구독자 추이 배치 시작");
      JobExecution execution = jobLauncher.run(playlistHistoryJob, jobParameters);
      log.info("구독자 추이 배치 상태 {}", execution.getStatus());
    } catch (Exception e) {
      log.error("구독자 추이 배치 실패 {}",e.getMessage());
    }
  }
}
