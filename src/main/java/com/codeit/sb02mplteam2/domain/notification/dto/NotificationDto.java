package com.codeit.sb02mplteam2.domain.notification.dto;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationDto (
    Long id,
    LocalDateTime createdAt,
    Long receiverId,
    String title,
    String content,
    NotificationType type,
    Long targetId,
    Long publisherId
) {

  public static NotificationDto from(Notification notification) {
    LocalDateTime createdAt = LocalDateTime.now();
    return NotificationDto.builder()
        .id(notification.getId())
        .createdAt(createdAt)
        .receiverId(notification.getReceiverId())
        .title(notification.getTitle())
        .content(notification.getContent())
        .type(notification.getType())
        .targetId(notification.getTargetId())
        .publisherId(notification.getPublisherId())
        .build();
  }
}
