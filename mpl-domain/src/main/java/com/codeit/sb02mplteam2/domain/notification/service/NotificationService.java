package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
  List<NotificationDto> findByLastEventTime(Long userId, LocalDateTime lastEventTime);

  void delete(Long notificationId);

  void deleteAllByUserId(Long userId);

  <T> NotificationDto save(UserDto receiver, UserDto publisher, NotificationType type, T target);
}
