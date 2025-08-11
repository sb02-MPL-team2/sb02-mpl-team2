package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.event.BroadcastEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NotificationService {
  List<NotificationDto> findByLastEventTime(Long userId, LocalDateTime lastEventTime);

  void delete(Long notificationId);

  void deleteAllByUserId(Long userId);

  NotificationDto broadcast(BroadcastEvent event);
  // targetId = 보내는 사람 혹은 채널 Id, 비동기 실패시 null
  NotificationDto create(Long receiverId, NotificationType notificationType, Long targetId, Long publisherId);
  // 전체 알림 생성

  List<NotificationDto> createAll(Set<Long> receiverIds, NotificationType notificationType, Long targetId, Long publisherId);

}
