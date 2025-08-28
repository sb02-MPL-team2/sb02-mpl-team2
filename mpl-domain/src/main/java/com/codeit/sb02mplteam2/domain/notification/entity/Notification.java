package com.codeit.sb02mplteam2.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "receiver_id", columnDefinition = "BIGINT")
  private Long receiverId;

  @Column(name = "publisher_id", columnDefinition = "BIGINT")
  private Long publisherId;

  @Column(name = "target_id", columnDefinition = "BIGINT")
  private Long targetId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  public static Notification of(Long receiverId, Long publisherId, Long targetId,String title, String content, NotificationType type) {
    return Notification.builder()
        .receiverId(receiverId)
        .createdAt(LocalDateTime.now())
        .publisherId(publisherId)
        .targetId(targetId)
        .title(title)
        .content(content != null ? content : "") // content가 null일 경우를 대비해 기본값 처리
        .type(type)
        .build();
  }
}
