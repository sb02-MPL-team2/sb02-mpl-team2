package com.codeit.sb02mplteam2.domain.livewatch.controller.websocket;

import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
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
      java.security.Principal principal = event.getUser();
      if (principal == null) {
        log.warn("WebSocket 연결 해제 - Principal이 null입니다: sessionId={}", sessionId);
        return;
      }

      Long userId = extractUserId(principal);
      if (userId == null) {
        log.warn("WebSocket 연결 해제 - userId 추출 실패: sessionId={}", sessionId);
        return;
      }

      liveWatchService.handleUserDisconnect(userId);
      log.info("사용자 {} WebSocket 비정상 종료 처리 위임 완료", userId);

    } catch (Exception e) {
      log.error("WebSocket 연결 해제 처리 중 오류 발생: sessionId={}", sessionId, e);
    }
  }

  private static String getSessionId(Message<byte[]> event) {
    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event);
    String sessionId = headerAccessor.getSessionId();
    return sessionId;
  }

  private Long extractUserId(java.security.Principal principal) {
    try {
      if (principal instanceof MplUserDetails userDetails) {
        return userDetails.getId();
      }

      // MplUserDetails가 아닌 경우 principal.getName()에서 ID 파싱 시도
      String principalName = principal.getName();
      if (principalName != null && !principalName.isEmpty()) {
        return Long.parseLong(principalName);
      }

      return null;
    } catch (NumberFormatException e) {
      log.error("MplUserDetails외의 Principal에서 userId 추출 실패: {}", principal.getName(), e);
      return null;
    }
  }
}