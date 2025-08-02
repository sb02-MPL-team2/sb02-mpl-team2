package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.ConnectionManager;
import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
  private final ConnectionManager connectionManager;
  private final NotificationRepository notificationRepository;

  //연결된 탭마다 알림 전송하기
  public void sendNotification(Long userId, NotificationDto notificationDto) {
    List<SseEmitter> userEmitters = connectionManager.getConnection(userId);
    if (userEmitters == null || userEmitters.isEmpty()) {
      log.warn("사용자에게 연결된 Emitter가 없습니다: userId={}", userId);
      return;
    }
    log.info("사용자에게 알림 전송 시도: userId={}, Emitter 개수={}", userId, userEmitters.size());

    //eventId = notification ID
    String eventId = notificationDto.id().toString();
    String eventName = "notification";

    userEmitters.forEach(emitter -> {
      log.info("알림 전송 시도: userId={}, eventId={}", userId, eventId);
      sendToEmitter(emitter, eventId, eventName, notificationDto);
    });
  }

  public void sendToEmitter(SseEmitter emitter, String eventId, String eventName, Object data) {
    try {
      log.info("SSE 이벤트 전송중...");
      emitter.send(SseEmitter.event()
          .id(eventId)
          .name(eventName)
          .data(data, MediaType.APPLICATION_JSON));
      log.info("SSE 이벤트 전송완료...");
    } catch (IOException e) {
      log.error("SSE 이벤트 전송 실패: eventId={}, eventName={}, error={}", eventId, eventName,
          e.getMessage());
    }
  }

  public void sendLostData(Long userId, String lastEventId, SseEmitter emitter) {
    // lastEventId 파싱
    // event-ID번호로 오기에 -앞까지 잘라야함
    String[] parts = lastEventId.split("-");
    if (parts.length < 1) {
      log.warn("잘못된 형식의 Last-Event-ID 입니다: {}", lastEventId);
      return;
    }

    try {
      List<Notification> lostNotifications = notificationRepository.findAllByReceiverIdAndIdAfter(userId,Long.parseLong(parts[1]));
      int count = 0;
      if (!lostNotifications.isEmpty()) {
        log.info("유실된 데이터 전송 시작: userId={}, lastEventId={}, count={}", userId, lastEventId,
            lostNotifications.size());
        for (Notification notification : lostNotifications) {
          count++;
          if (notification.getReceiverId().equals(userId)) {
            sendToEmitter(emitter, notification.getId().toString(), "notification",
                NotificationDto.from(notification));
          }
        }
        log.info("{} 개 유실된 데이터 전송 완료.", count);
      }
    } catch (Exception e) {
      log.error("유실된 데이터 처리 중 오류 발생: lastEventId={}, error={}", lastEventId, e.getMessage(), e);
    }
  }
}
