package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicLiveWatchBroadcastService implements LiveWatchBroadcastService {

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void broadcastMessage(Long roomId, ChatMessageDto message) {
    messagingTemplate.convertAndSend("/topic/livewatch/rooms/" + roomId + "/messages", message);
    log.info("채팅방 {}에 메시지 브로드캐스트: {}", roomId, message.content());
  }

  @Override
  public void broadcastParticipantEvent(Long roomId, ChatMessageDto eventMessage) {
    messagingTemplate.convertAndSend("/topic/livewatch/rooms/" + roomId + "/events", eventMessage);
    log.info("채팅방 {}에 참여자 이벤트 브로드캐스트: {}", roomId, eventMessage.content());
  }

  @Override
  public void sendErrorToUser(String username, ErrorNotificationDto error) {
    messagingTemplate.convertAndSendToUser(username, "/queue/errors", error);
    log.warn("사용자 {}에게 에러 전송: {}", username, error.message());
  }

  @Override
  public void broadcastSystemMessage(Long roomId, String message) {
    messagingTemplate.convertAndSend("/topic/livewatch/rooms/" + roomId + "/system", message);
    log.info("채팅방 {}에 시스템 메시지 브로드캐스트: {}", roomId, message);
  }
}