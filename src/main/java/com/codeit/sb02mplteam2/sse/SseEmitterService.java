package com.codeit.sb02mplteam2.sse;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseEmitterService {
  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30;
  private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

  public SseEmitter subscribe(Long userId) {
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitters.putIfAbsent(userId, new CopyOnWriteArrayList<>());
    emitters.get(userId).add(sseEmitter);

    log.info("새 SSE 연결 : userId = {}, emitter={}", userId, sseEmitter);
    sseEmitter.onCompletion(() -> removeEmitter(userId, sseEmitter, "onCompletion"));
    sseEmitter.onTimeout(() -> removeEmitter(userId, sseEmitter, "onTimeout"));
    sseEmitter.onError(e -> removeEmitter(userId, sseEmitter, "onError: " + e.getMessage()));

    return sseEmitter;
  }

  public void sendNotification(Long userId, NotificationDto notification) {
    List<SseEmitter> userEmitters = emitters.get(userId);
    if (userEmitters == null || userEmitters.isEmpty()) {
      log.warn("사용자에게 연결된 Emitter가 없습니다: userId={}", userId);
      return;
    }
    log.info("사용자에게 알림 전송 시도: userId={}, Emitter 개수={}", userId, userEmitters.size());

    String eventId =  notification.id().toString();
    String eventName = "notification";

    userEmitters.forEach(emitter -> {
      log.info("알림 전송 시도: userId={}, eventId={}", userId, eventId);
      sendToEmitter(emitter, eventId, eventName, notification);
    });
  }

  private void sendToEmitter(SseEmitter emitter, String eventId, String eventName, Object data) {
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


  private void removeEmitter(Long userId, SseEmitter emitter, String reason) {
    List<SseEmitter> emitterList = emitters.get(userId);
    if (emitterList != null) {
      boolean removed = emitterList.remove(emitter);
      if (removed) {
        log.info("Emitter 제거 완료 : userId={}, reason={}, remainingEmitters={}", userId, reason,
            emitterList.size());

        if (emitterList.isEmpty()) {
          emitters.remove(userId);
          log.info("userId = {}의 Emitter 리스트 비어있어서 맵에서 제거", userId);
        }
      }
    }
  }
}
