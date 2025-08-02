package com.codeit.sb02mplteam2.domain.notification.controller;

import com.codeit.sb02mplteam2.domain.notification.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  // last Event ID = notification ID
  @GetMapping(value = "/sse/{userId}")
  public ResponseEntity<SseEmitter> sse(
      @PathVariable Long userId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
  ) {
    log.info("SSE 연결 요청: userId={}", userId);
    SseEmitter sseEmitter = connectionManager.subscribe(userId, lastEventId);
    return ResponseEntity.ok(sseEmitter);
  }

}
