package com.codeit.sb02mplteam2.domain.notification.dto;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationDto (
    Long id,
    Long receiverId,
    Long publisherId,
    Long targetId,
    LocalDateTime createdAt,
    NotificationType type,
    String title,
    String content
) {

  public static NotificationDto of(Notification notification) {
    return NotificationDto.builder()
        .id(notification.getId())
        .receiverId(notification.getReceiverId())
        .publisherId(notification.getPublisherId())
        .targetId(notification.getTargetId())
        .createdAt(notification.getCreatedAt())
        .type(notification.getType())
        .title(notification.getTitle())
        .content(notification.getContent())
        .build();
  }

  public static NotificationDto of(Long targetId,NotificationType type,String title, String content, LocalDateTime createdAt) {
    return NotificationDto.builder()
        .targetId(targetId)
        .createdAt(createdAt)
        .type(type)
        .title(title)
        .content(content)
        .build();
  }
}
