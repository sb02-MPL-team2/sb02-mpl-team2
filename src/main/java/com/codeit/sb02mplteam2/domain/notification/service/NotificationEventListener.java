package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationService notificationService;

  @Async
  @EventListener
  @Transactional
  public void handleNotificationEvent(NotificationEvent event) {
    log.info("알림 이벤트 발생 type={}, 받는 이 ID={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
        event.getNotificationType(), event.getReceiverId(), event.getTargetId(),
        event.getPublisherId());

    notificationService.create(event.getReceiverId(), event.getNotificationType(),
        event.getTargetId(), event.getPublisherId());

  }

}
