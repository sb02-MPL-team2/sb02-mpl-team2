package com.codeit.sb02mplteam2.domain.task;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.event.BulkNotificationSendEvent;
import com.codeit.sb02mplteam2.event.NotificationSendEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public void sendNotification(Notification notification) {
    NotificationDto notificationDto = NotificationDto.of(notification);
    NotificationSendEvent event = new NotificationSendEvent(this, notificationDto);
    eventPublisher.publishEvent(event);
  }

  public void sendBulkNotification(List<Notification> notificationList) {
    if (notificationList != null && !notificationList.isEmpty()) {
      BulkNotificationSendEvent sendEvent = new BulkNotificationSendEvent(notificationList);
      eventPublisher.publishEvent(sendEvent);
    }
  }
}
