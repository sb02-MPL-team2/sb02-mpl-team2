package com.codeit.sb02mplteam2.domain.task;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.event.BulkNotificationSendEvent;
import com.codeit.sb02mplteam2.util.RabbitConst;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationProducer {

  private final RabbitTemplate rabbitTemplate;

  public void sendNotification(Notification notification) {
    rabbitTemplate.convertAndSend(RabbitConst.notificationExchange,
        RabbitConst.Notification_Receive_RoutingKey, notification);
  }

  public void sendBulkNotification(List<Notification> notificationList) {
    if (notificationList != null && !notificationList.isEmpty()) {
      BulkNotificationSendEvent sendEvent = new BulkNotificationSendEvent(notificationList);
      rabbitTemplate.convertAndSend(RabbitConst.notificationExchange,
          RabbitConst.Notification_Bulk_Receive_RoutingKey, sendEvent);
    }
  }
}
