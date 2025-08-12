package com.codeit.sb02mplteam2.domain.content.controller;

import com.codeit.sb02mplteam2.domain.content.dto.tmdb.BatchResponseDto;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

  private final JobLauncher jobLauncher;
  private final Job tmdbContentUpdateJob;

  @PostMapping("/tmdb/update")
  public ResponseEntity<?> runTmdbUpdate() throws Exception {
    try {
      JobParameters params = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();

      JobExecution exec = jobLauncher.run(tmdbContentUpdateJob, params);

      return ResponseEntity.ok(
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
