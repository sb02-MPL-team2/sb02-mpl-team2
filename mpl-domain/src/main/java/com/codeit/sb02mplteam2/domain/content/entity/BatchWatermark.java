package com.codeit.sb02mplteam2.domain.content.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "batch_watermark")
public class BatchWatermark {

  @Id
  private String taskKey;

  private LocalDate lastProcessedDate;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public BatchWatermark(String taskKey, LocalDate lastProcessedDate) {
    this.taskKey = taskKey;
    this.lastProcessedDate = lastProcessedDate;
  }

  public void updateLastProcessedDate(LocalDate newDate) {
    this.lastProcessedDate = newDate;
  }
}