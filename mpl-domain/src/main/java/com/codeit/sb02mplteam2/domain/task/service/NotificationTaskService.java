package com.codeit.sb02mplteam2.domain.task.service;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.event.BulkNotificationTaskEvent;
import com.codeit.sb02mplteam2.event.NotificationTaskEvent;
import java.util.List;
import java.util.Set;

public interface NotificationTaskService {

  Notification create(NotificationTaskEvent event);

  List<Notification> create(BulkNotificationTaskEvent bulkEvent);

  Notification create(Long receiverId, Long publisherId, NotificationType type, Long targetId);

  List<Notification> createAll(Set<Long> receiverIds, Long publisherId,
      NotificationType type, Long target);
}
