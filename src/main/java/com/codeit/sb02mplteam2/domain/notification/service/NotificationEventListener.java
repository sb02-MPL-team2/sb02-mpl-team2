package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.FilteringProcessor;
import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.notification.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.event.NotificationEvent;
import java.util.List;
import java.util.Set;
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
    Long receiverId = event.getReceiverId();
    log.info("알림 이벤트 발생 type={}, 받는 이 ID={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
        event.getNotificationType(), receiverId, event.getTargetId(),
        event.getPublisherId());

    NotificationDto notificationDto = notificationService.create(receiverId,
        event.getNotificationType(),
        event.getTargetId(), event.getPublisherId());

    if (notificationDto == null) {
      log.warn("보낼 메시지가 존재하지 않습니다.");
      return;
    }

    ConnectionInfo connectionInfo = filteringProcessor.filterTargetClient(receiverId);
    if (connectionInfo == null) {
      log.warn("연결이 존재하지 않습니다.");
      return;
    }

    deliveryService.deliverToClient(connectionInfo, notificationDto);
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBulkNotificationEvent(BulkNotificationEvent event) {
    Set<Long> receiverIds = event.getReceiverIds();
    log.info("알림 이벤트 발생 type={}, 받는 이들의 수={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
        event.getNotificationType(), receiverIds.size(), event.getTargetId(), event.getPublisherId());

    List<NotificationDto> notificationDtoList = notificationService.createAll(receiverIds,
        event.getNotificationType(), event.getTargetId(), event.getPublisherId());

    List<ConnectionInfo> connectionInfoList = filteringProcessor.filterTargetClients(receiverIds);

    if (connectionInfoList.isEmpty()) {
      return;
    }

    deliveryService.deliverNotifications(notificationDtoList.get(0), connectionInfoList);
  }
}
