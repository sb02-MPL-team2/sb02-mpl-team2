package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
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

  public void deliverNotifications(NotificationDto notificationDto,
      List<ConnectionInfo> targetClients) {
    if (targetClients.isEmpty()) {
      return;
    }

    for (ConnectionInfo connectionInfo : targetClients) {
      deliverToClient(connectionInfo, notificationDto);
    }
  }

  public void deliverToClient(ConnectionInfo connectionInfo, NotificationDto notificationDto) {
    String eventId = notificationDto.id().toString();
    String eventName = notificationDto.type().name();
    try {
      SseEmitter emitter = connectionInfo.getSseEmitter();
      log.info("SSE 이벤트 전송중...");
      emitter.send(SseEmitter.event()
          .id(eventId)
          .name(eventName)
          .data(notificationDto, MediaType.APPLICATION_JSON));
      log.info("SSE 이벤트 전송완료...");
    } catch (IOException e) {
      log.error("SSE 이벤트 전송 실패: eventId={}, eventName={}, error={}", eventId, eventName,
          e.getMessage());
    }
    connectionInfo.updateLastActiveAt();
  }
}
