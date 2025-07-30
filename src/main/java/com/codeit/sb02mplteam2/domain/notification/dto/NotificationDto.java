package com.codeit.sb02mplteam2.domain.notification.dto;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto (
    Long id,
    LocalDateTime createdAt,
    Long receiverId,
    String title,
    String content,
    NotificationType type,
    Long targetId,
    Long publisherId
)
{
}
