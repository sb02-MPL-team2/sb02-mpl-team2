package com.codeit.sb02mplteam2.domain.livewatch.controller.websocket;

import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiveChatStompController {

    private final LiveWatchService liveWatchService;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        // TODO: Auth 레이어에서 JWT 검증한거 가져오기
        Long userId = 1L;
        
        liveWatchService.sendMessage(request, userId);
        
        log.info("Message received from user {} to room {}: {}", 
                userId, request.liveChatRoomId(), request.content());
    }
}