package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;
import com.codeit.sb02mplteam2.domain.livewatch.redis.RedisStreamsBroadcaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicLiveWatchBroadcastService implements LiveWatchBroadcastService {

  private final RedisStreamsBroadcaster redisStreamsBroadcaster;

  @Override
  public void broadcastMessage(Long roomId, ChatMessageDto message) {
    String destination = "/topic/livewatch/rooms/" + roomId + "/messages";
    redisStreamsBroadcaster.broadcastMessage(destination, message);
    log.info("채팅방 {}에 메시지 브로드캐스트: {}", roomId, message.content());
  }

  @Override
  public void broadcastParticipantEvent(Long roomId, ChatMessageDto eventMessage) {
    String destination = "/topic/livewatch/rooms/" + roomId + "/events";
    redisStreamsBroadcaster.broadcastParticipantEvent(destination, eventMessage);
    log.info("채팅방 {}에 참여자 이벤트 브로드캐스트: {}", roomId, eventMessage.content());
  }

  @Override
  public void sendErrorToUser(String username, ErrorNotificationDto error) {
    redisStreamsBroadcaster.sendErrorToUser(username, error);
    log.warn("사용자 {}에게 에러 전송: {}", username, error.message());
  }

  @Override
  public void broadcastSystemMessage(Long roomId, String message) {
    String destination = "/topic/livewatch/rooms/" + roomId + "/system";
    redisStreamsBroadcaster.broadcastSystemMessage(destination, message);
    log.info("채팅방 {}에 시스템 메시지 브로드캐스트: {}", roomId, message);
  }
}