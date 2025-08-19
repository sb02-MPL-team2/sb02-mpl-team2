//package com.codeit.sb02mplteam2.domain.social.controller;
//
//import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
//import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
//import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageWsResponse;
//import com.codeit.sb02mplteam2.domain.social.service.DirectMessageService;
//import com.codeit.sb02mplteam2.security.MplUserDetails;
//import java.security.Principal;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class DirectMessageWsController {
//
//  private final DirectMessageService directMessageService;
//  private final SimpMessagingTemplate messagingTemplate;
//
//  @MessageMapping("/dm/send") // 클라이언트는 /app/dm/send 로 보냄
//  public void sendMessage(DirectMessageCreateRequest request, Principal principal) {
//    Long senderId = ((MplUserDetails)((UsernamePasswordAuthenticationToken) principal).getPrincipal())
//        .getUserDto().id();
//
//    request = new DirectMessageCreateRequest(request.channelId(), senderId, request.content());
//    DirectMessageResponse saved = directMessageService.create(request);
//
//    Long receiverId = directMessageService.findReceiverId(saved.channelId(), senderId);
//
//    DirectMessageWsResponse wsMessage = new DirectMessageWsResponse(saved, receiverId);
//
//    messagingTemplate.convertAndSendToUser(
//        String.valueOf(receiverId),
//        "/queue/dm/messages",
//        wsMessage
//    );
//
//    messagingTemplate.convertAndSendToUser(
//        String.valueOf(senderId),
//        "/queue/dm/messages",
//        wsMessage
//    );
//
//    log.info("[WebSocket DM] sender: {}, receiver: {}, content: {}",
//        senderId, receiverId, saved.content());
//  }
//}
