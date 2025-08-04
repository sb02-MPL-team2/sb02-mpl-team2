package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConnectionManager {

  private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30; //1000 밀리초 * 1 밀리초 = 1초 * 60 초 = 1분 * 30 = 30분 타임아웃
  private static final int INACTIVE_TIMEOUT_MINUTES = 30;
  private final AtomicInteger connectionCount = new AtomicInteger(0); //현재 연결된 수
  private static final int MAX_CONNECTIONS = 100; //최대 연결 수
  //열려져 있는 탭마다 Emitters 연결함
  private final Map<Long, ConnectionInfo> connections = new ConcurrentHashMap<>();// SSE 연결 메모리에 저장

  public Optional<SseEmitter> subscribe(Long userId) {
    if (connectionCount.get() >= MAX_CONNECTIONS) {
      log.warn("SSE 연결이 최대가 되었습니다.");
      return Optional.empty();
    }
    //TODO 여러 스레드에서 동시에 실행할 경우, 최대 연결을 넘어설 수 있는 문제 존재함
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
    ConnectionInfo connectionInfo = new ConnectionInfo(userId, sseEmitter);
    connections.put(userId, connectionInfo);
    connectionCount.incrementAndGet(); // 연결 카운트 증가

    log.info("새 SSE 연결 : userId = {}, emitter={}", userId, sseEmitter);
    sseEmitter.onCompletion(() -> removeConnection(userId, "onCompletion"));
    sseEmitter.onTimeout(() -> removeConnection(userId, "onTimeout"));
    sseEmitter.onError(e -> removeConnection(userId, "onError: " + e.getMessage()));

    return Optional.of(sseEmitter);
  }

  public ConnectionInfo getConnection(Long userId) {
    return connections.get(userId);
  }

  public List<ConnectionInfo> getConnectionIn(Set<Long> userIds) {
    return userIds.stream()
        .map(connections::get)
        .filter(Objects::nonNull)
        .toList();
  }

  public List<ConnectionInfo> getConnections() {
    return List.copyOf(connections.values());
  }

  private void removeConnection(Long userId, String reason) {
    ConnectionInfo connectionInfo = connections.remove(userId);
    if (connectionInfo != null) {
      try {
        connectionInfo.getSseEmitter().complete();
        log.info("Emitter 제거 완료 : userId={}, reason={}", userId, reason);
      } catch (Exception e) {
        log.warn("이미 종료된 연결입니다.");
      }
    }
    connectionCount.decrementAndGet(); // 연결 카운트 감소
    log.info("클라이언트 연결 해제: {} (총 {}개)", userId, connections.size());
  }

  @Scheduled(cron = "0 0/5 * * * *")
  public void checkInactiveConnections() {
    LocalDateTime cutoff = LocalDateTime.now().minusMinutes(INACTIVE_TIMEOUT_MINUTES);
    connections.entrySet().removeIf(entry -> {
      Long userId = entry.getKey();
      ConnectionInfo connectionInfo = entry.getValue();
      if (connectionInfo.getLastActiveAt().isBefore(cutoff)) {
        try {
          connectionInfo.getSseEmitter().send(SseEmitter.event()
              .name("heartbeat")
              .data("ping"));
          connectionInfo.updateLastActiveAt();
          return false;
        } catch (Exception e) {
          connectionCount.decrementAndGet();
          log.info("비활성 연결 정리 : {}", userId);
          return true;
        }
      }
      return false;
    });
  }
}
