package com.codeit.sb02mplteam2.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

  private final SseEmitterService sseEmitterService;

  @GetMapping(value = "/sse")
  public ResponseEntity<SseEmitter> sse() {
    //TODO mockId 지워야함
    Long mockId = 1000000L;
    return ResponseEntity.ok(sseEmitterService.subscribe(mockId));
  }

}
