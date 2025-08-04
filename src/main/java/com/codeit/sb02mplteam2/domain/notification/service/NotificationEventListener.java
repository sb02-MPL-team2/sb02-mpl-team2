package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.FilteringProcessor;
import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final FilteringProcessor filteringProcessor;
  private final NotificationService notificationService;
  private final DeliveryService deliveryService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleNotificationEvent(NotificationEvent event) {
    log.info("알림 이벤트 발생 type={}, 받는 이 ID={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
        event.getNotificationType(), event.getReceiverId(), event.getTargetId(),
        event.getPublisherId());

    NotificationDto notificationDto = notificationService.create(event.getReceiverId(),
        event.getNotificationType(),
        event.getTargetId(), event.getPublisherId());

    if (notificationDto == null) {
      log.warn("보낼 메시지가 존재하지 않습니다.");
      return;
    }

    ConnectionInfo connectionInfo = filteringProcessor.filterTargetClient(event.getReceiverId());
    deliveryService.deliverToClient(connectionInfo, notificationDto );
  }

  /*
    아직 로직이 불안정함
    로직 추가 개선 가능해보임
   */
//  @Async
//  @EventListener
//  public void handleBulkNotificationEvent(BulkNotificationEvent event) {
//    log.info("알림 이벤트 발생 type={}, 받는 이들의 수={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
//        event.getNotificationType(), event.getReceiverIds().size(), event.getTargetId(), event.getPublisherId());
//
//    List<NotificationDto> notificationDtoList = notificationService.createAll(event.getReceiverIds(),
//        event.getNotificationType(), event.getTargetId(), event.getPublisherId());
//
//    for (NotificationDto notificationDto : notificationDtoList) {
//      List<ConnectionInfo> connectionInfos = eventProcessor.filterTargetClients(notificationDto);
//      deliveryService.deliverNotifications(notificationDto, connectionInfos);
//    }
//  }
}
