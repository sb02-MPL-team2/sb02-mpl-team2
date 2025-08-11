package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.FilteringProcessor;
import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.notification.event.BroadcastEvent;
import com.codeit.sb02mplteam2.domain.notification.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.event.LostNotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.event.NotificationEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void broadcast(BroadcastEvent event) {

    log.info("브로드캐스트 이벤트 처리 시작: type={}, targetId={}",
        event.getNotificationType(), event.getTargetId());

    //추천 알람 허용한 유저의 연결만 가져옴
    List<ConnectionInfo> connectionInfoList = filteringProcessor.broadcastClients();

    if (connectionInfoList.isEmpty()) {
      log.warn("브로드 캐스트 연결이 존재하지 않습니다.");
      return;
    }

    NotificationDto broadcast = notificationService.broadcast(event);

    connectionInfoList.forEach(connectionInfo -> {
      deliveryService.deliverToClient(connectionInfo, broadcast);
    });
  }

  @Async("notificationExecutor")
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

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBulkNotificationEvent(BulkNotificationEvent event) {
    Set<Long> receiverIds = event.getReceiverIds();
    log.info("알림 이벤트 발생 type={}, 받는 이들의 수={}, 발생된 장소 ID={}, 이벤트 발생시킨 이 ID={}",
        event.getNotificationType(), receiverIds.size(), event.getTargetId(),
        event.getPublisherId());

    List<NotificationDto> notificationDtoList = notificationService.createAll(receiverIds,
        event.getNotificationType(), event.getTargetId(), event.getPublisherId());

    if (notificationDtoList.isEmpty()) {
      log.warn("생성된 알림이 없어 전송을 중단합니다.");
      return;
    }

    List<ConnectionInfo> targetConnections = filteringProcessor.filterTargetClients(receiverIds);

    Map<Long, ConnectionInfo> connectionInfoMap = targetConnections.stream()
        .collect(Collectors.toMap(ConnectionInfo::getUserId, conn -> conn));

    notificationDtoList.forEach(dto->{
      ConnectionInfo connectionInfo = connectionInfoMap.get(dto.receiverId());
      if (connectionInfo != null) {
        // 연결이 존재하는 경우에만 즉시 전송
        deliveryService.deliverToClient(connectionInfo, dto);
      } else {
        log.info("SSE 클라이언트가 연결되어 있지 않아 실시간 전송은 생략합니다. receiverId={}", dto.receiverId());
      }
    });

    log.info("벌크 알림 실시간 전송 완료. 총 {}개 중 {}개 성공.", notificationDtoList.size(), connectionInfoMap.size());
  }

  @Async("notificationExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleLostNotificationEvent(LostNotificationEvent event) {
    Long receiverId = event.getUserId();
    LocalDateTime lastEventTime = event.getLastEventTime();
    log.info("마지막 알람 전송 이후 미전송된 알람 전송 : 유저 아이디 {}, 마지막 전송 시각 {}", receiverId, lastEventTime);
    List<NotificationDto> lostNotificationList = notificationService.findByLastEventTime(receiverId,
        event.getLastEventTime());

    if (lostNotificationList.isEmpty()) {
      log.warn("미전송된 알람이 존재하지 않습니다.");
      return;
    }
    for (NotificationDto notificationDto : lostNotificationList) {
      ConnectionInfo connectionInfo = filteringProcessor.filtering(notificationDto.type(),
          event.getConnectionInfo());
      if (connectionInfo == null) {
        continue;
      }
      deliveryService.deliverToClient(connectionInfo, notificationDto);
    }
  }
}
