package com.codeit.sb02mplteam2.domain.content.controller;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.BatchResponseDto;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class BatchController {

  private final JobLauncher jobLauncher;
  private final Job importTmdbJob;
  private final JobExplorer jobExplorer;

  @PostMapping("/tmdb/run")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<BatchResponseDto> run(
      @RequestParam(defaultValue = "ALL") String category,
      @RequestParam(defaultValue = "10") int maxPages,
      @RequestParam(defaultValue = "300") int rateLimitMs
  ) {
    if (!jobExplorer.findRunningJobExecutions("importTmdbJob").isEmpty()) {
      return ResponseEntity.status(409)
          .body(new BatchResponseDto(null, "importTmdbJob", "RUNNING", "ALREADY_RUNNING"));
    }

    try {
      JobParameters params = new JobParametersBuilder()
          .addLong("runId", System.currentTimeMillis())
          .addString("category", category)
          .addLong("maxPages", (long) maxPages)
          .addLong("rateLimitMs", (long) rateLimitMs)
          .toJobParameters();

      JobExecution exec = jobLauncher.run(importTmdbJob, params);

      return ResponseEntity.accepted().body(
          new BatchResponseDto(
              exec.getId(),
              exec.getJobInstance().getJobName(),
              exec.getStatus().toString(),
              exec.getExitStatus().getExitCode()
          )
      );

    } catch (Exception e) {
      throw new MplException(ErrorCode.INTERNAL_SERVER_ERROR, e);
    }
  }
}