package com.codeit.sb02mplteam2.domain.livewatch.controller.websocket;

import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiveWebSocketEventHandler {

  private final LiveWatchService liveWatchService;

  @EventListener
  public void handleWebSocketConnect(SessionConnectedEvent event) {
    String sessionId = getSessionId(event.getMessage());
    log.info("WebSocket 연결됨: sessionId={}", sessionId);

    // TODO: 필요시 연결 통계 수집 등
  }


  @EventListener
  public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
    String sessionId = getSessionId(event.getMessage());
    log.info("WebSocket 연결 해제됨: sessionId={}", sessionId);

    try {
      Long userId = extractUserIdFromSession(event.getMessage());
      
      if (userId == null) {
        log.warn("[WebSocket] 연결 해제 - 인증되지 않은 세션: sessionId={}", sessionId);
        return;
      }

      liveWatchService.handleUserDisconnect(userId);
      log.info("[WebSocket] 사용자 {} WebSocket 연결 해제 처리 완료", userId);

    } catch (Exception e) {
      log.error("[WebSocket] 연결 해제 처리 중 오류 발생: sessionId={}", sessionId, e);
    }
  }

  private static String getSessionId(Message<byte[]> event) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event);
    String sessionId = headerAccessor.getSessionId();
    return sessionId;
  }

  private Long extractUserIdFromSession(Message<byte[]> message) {
    try {
      SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
      Object userId = headerAccessor.getSessionAttributes().get("userId");
      
      if (userId instanceof Long) {
        return (Long) userId;
      } else if (userId != null) {
        return Long.valueOf(userId.toString());
      }
      
      return null;
    } catch (Exception e) {
      log.debug("[WebSocket] 세션 속성에서 userId 추출 실패", e);
      return null;
    }
  }

}