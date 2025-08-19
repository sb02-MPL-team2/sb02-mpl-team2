package com.codeit.sb02mplteam2.domain.livewatch.controller.websocket;

import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveWatchStompController {

  private final LiveWatchService liveWatchService;

  @MessageMapping("/livewatch/send")
  public void sendMessage(@Payload SendMessageRequest request,
      @AuthenticationPrincipal MplUserDetails userDetails) {
    Long userId = userDetails.getId();

    liveWatchService.sendMessage(request, userId);
    log.info("[WebSocket] LiveWatch 메시지 전송 완료 - userId: {}, username: {}, roomId: {}", userId, userDetails.getUsername(), request.liveWatchRoomId());
  }

}