package com.codeit.sb02mplteam2.domain.task;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.task.service.NotificationTaskService;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import com.codeit.sb02mplteam2.util.RabbitConst;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationTaskListener {

  private final NotificationTaskService taskService;
  private final NotificationProducer notificationProducer;

  @RabbitListener(queues = RabbitConst.taskQueue)
  public void handlerNotificationTaskEvent(NotificationEvent event) {
    log.info("알람 생성 작업을 진행합니다.");
    Notification notification = taskService.create(event);
    notificationProducer.sendNotification(notification);
  }

  @RabbitListener(queues = RabbitConst.taskBulkQueue)
  public void handlerBulkNotificationTaskEvent(BulkNotificationEvent event) {
    log.info("알람 대량 생성 작업을 진행합니다.");
    List<Notification> notificationList = taskService.create(event);
    notificationProducer.sendBulkNotification(notificationList);
  }
}
