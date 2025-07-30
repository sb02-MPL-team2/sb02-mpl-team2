package com.codeit.sb02mplteam2.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
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

  @Column(name = "receiver_id", columnDefinition = "BIGINT", nullable = false)
  private Long receiverId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(name = "target_id", columnDefinition = "BIGINT")
  private Long targetId;

  @Column(name = "publisher_id", columnDefinition = "BIGINT")
  private Long publisherId;

  public Notification(Long receiverId, String title, String content, NotificationType type) {
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
    this.type = type;
  }

  public Notification(Long receiverId, String title, String content, NotificationType type,
      Long targetId) {
    this(receiverId, title, content, type);
    this.targetId = targetId;
  }
}
