package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.service.DeliveryService;
import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import com.codeit.sb02mplteam2.domain.task.service.NotificationTaskService;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

  private final NotificationTaskService notificationTaskService;
  private final DeliveryService deliveryService;
  private final NotificationService notificationService;

  /**
   * Task Service가 이벤트를 처리하도록 메시지를 전달
   */
  public void delegateToTaskService(NotificationEvent originalEvent) {
    // DB 조회가 필요한 작업을 처리하는 별도의 큐로 메시지를 보냅니다.
    log.info("[Slow Path] Event를 Task Service로 위임합니다. Event: {}", originalEvent);

    Notification notification = notificationTaskService.create(originalEvent);
    NotificationDto notificationDto = NotificationDto.of(notification);
    deliveryService.deliverToClient(notificationDto);
  }

  /**
   * Task Service가 Bulk 이벤트를 처리하도록 메시지를 전달하는 메서드
   */
  public void delegateBulkTaskToService(BulkNotificationEvent bulkEvent) {
    // DB 조회가 필요한 작업을 처리하는 별도의 벌크 작업 큐로 메시지를 보냅니다.
    log.info("[Slow Path] Event를 Task Service로 위임합니다. Event: {}, 수신자: {}", bulkEvent, bulkEvent.getReceiverIds().size());

    List<Notification> notificationList = notificationTaskService.create(bulkEvent);
    notificationList.forEach(
        notification -> deliveryService.deliverToClient(NotificationDto.of(notification)));
  }

  /**
   * 캐시된 데이터로 실제 알림을 처리하고 전송
   */
  public <T> void processNotificationWithCachedData(UserDto receiver, UserDto publisher,
      T targetEntity, NotificationType type) {
    // 직접 SSE 서비스를 호출하는 로직을 구현합니다.
    log.info("[Fast Path] receiver: {}, publisher: {}, target: {}, type: {}", receiver.id(),
        publisher.id(), targetEntity, type);
    NotificationDto notificationDto = notificationService.save(receiver, publisher, type,
        targetEntity);
    deliveryService.deliverToClient(notificationDto);
  }

  /**
   * 캐시된 데이터로 대규모 알림을 처리하고 전송하는 메서드
   */
  public void processBulkNotificationsWithCachedData(Set<UserDto> receivers, UserDto publisher,
      Object targetEntity, NotificationType type) {
    // receivers Set을 순회하며 각 사용자에게 보낼 NotificationDto를 생성하고,
    log.info("{}명에게 알림 DTO 생성 및 전송 로직 실행", receivers.size());
    List<NotificationDto> notificationDtoList = notificationService.saveAll(receivers, publisher,
        type, targetEntity);
    notificationDtoList.forEach(deliveryService::deliverToClient);
  }
}
