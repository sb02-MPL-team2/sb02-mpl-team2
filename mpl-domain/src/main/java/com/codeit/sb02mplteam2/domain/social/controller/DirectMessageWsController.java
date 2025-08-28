package com.codeit.sb02mplteam2.domain.social.controller;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.service.DirectMessageService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DirectMessageWsController {

  private final DirectMessageService directMessageService;

  @MessageMapping("/dm/send")
  public void sendMessage(@Payload DirectMessageCreateRequest request,
      @AuthenticationPrincipal MplUserDetails userDetails) {
    Long senderId = userDetails.getUserDto().id();

    DirectMessageCreateRequest finalRequest =
        new DirectMessageCreateRequest(senderId, request.channelId(), request.content());

    directMessageService.create(finalRequest);
  }
}
