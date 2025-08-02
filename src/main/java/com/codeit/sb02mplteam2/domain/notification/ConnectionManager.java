package com.codeit.sb02mplteam2.domain.notification;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConnectionManager {

  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30; //1000 밀리초 * 1 밀리초 = 1초 * 60 초 = 1분 * 30 = 30분 타임아웃
  private final AtomicInteger connectionCount = new AtomicInteger(0); //현재 연결된 수
  private static final int MAX_CONNECTIONS = 100; //최대 연결 수
  //열려져 있는 탭마다 Emitters 연결함
  private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();// SSE 연결 메모리에 저장

  public SseEmitter subscribe(Long userId, String lastEventId) {
    if (connectionCount.get() >= MAX_CONNECTIONS) {
      log.warn("SSE 연결이 최대가 되었습니다.");
      return null;
    }
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
    emitters.putIfAbsent(userId, new CopyOnWriteArrayList<>());
    emitters.get(userId).add(sseEmitter);
    connectionCount.incrementAndGet(); // 연결 카운트 증가

    log.info("새 SSE 연결 : userId = {}, emitter={}", userId, sseEmitter);
    sseEmitter.onCompletion(() -> removeEmitter(userId, sseEmitter, "onCompletion"));
    sseEmitter.onTimeout(() -> removeEmitter(userId, sseEmitter, "onTimeout"));
    sseEmitter.onError(e -> removeEmitter(userId, sseEmitter, "onError: " + e.getMessage()));

    if (lastEventId != null && !lastEventId.isEmpty()) {
      log.info("유실된 데이터 재전송");
    }

    return sseEmitter;
  }

  public List<SseEmitter> getConnection(Long userId) {
    return emitters.get(userId);
  }

  private void removeEmitter(Long userId, SseEmitter emitter, String reason) {
    List<SseEmitter> emitterList = emitters.get(userId);
    if (emitterList != null) {
      boolean removed = emitterList.remove(emitter);
      if (removed) {
        log.info("Emitter 제거 완료 : userId={}, reason={}, remainingEmitters={}", userId, reason,
            emitterList.size());
        connectionCount.decrementAndGet(); // 연결 카운트 감소
        if (emitterList.isEmpty()) {
          emitters.remove(userId);
          log.info("userId = {}의 Emitter 리스트 비어있어서 맵에서 제거", userId);
        }
      }
    }
  }
}
