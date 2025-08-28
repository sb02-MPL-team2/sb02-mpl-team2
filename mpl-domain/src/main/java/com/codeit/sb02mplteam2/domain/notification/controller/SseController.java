package com.codeit.sb02mplteam2.domain.notification.controller;

import com.codeit.sb02mplteam2.domain.notification.ConnectionManager;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

  private final ConnectionManager connectionManager;

  @GetMapping(produces = "text/event-stream")
  public ResponseEntity<SseEmitter> sse(
      @AuthenticationPrincipal MplUserDetails userDetails
//       last Event ID = "알람 생성 시간 + "_" + notification ID"
      , @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
  ) {
    Long userId = userDetails.getUserDto().id();
    log.info("SSE 연결 요청: userId={}, last-event-id= {}", userId, lastEventId);
    SseEmitter sseEmitter = connectionManager.subscribe(userId, lastEventId);

    if (sseEmitter == null) {
      return ResponseEntity.status(503).build();
    } else {
      return ResponseEntity.ok(sseEmitter);
    }
  }
}
