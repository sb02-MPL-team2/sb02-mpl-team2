package com.codeit.sb02mplteam2.domain.livewatch.redis;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamsBroadcaster {

  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;

  @Value("${livewatch.redis.streams.stream-key:livewatch:broadcast}")
  private String streamKey;

  public void broadcastMessage(String destination, ChatMessageDto message) {
    broadcast(destination, message, "CHAT_MESSAGE", null);
  }

  public void broadcastParticipantEvent(String destination, ChatMessageDto eventMessage) {
    broadcast(destination, eventMessage, "PARTICIPANT_EVENT", null);
  }

  public void sendErrorToUser(String username, ErrorNotificationDto error) {
    broadcast(null, error, "ERROR", username);
  }

  public void broadcastSystemMessage(String destination, String message) {
    broadcast(destination, message, "SYSTEM", null);
  }

  private void broadcast(String destination, Object payload, String messageType,
      String targetUsername) {
    try {
      String payloadJson = objectMapper.writeValueAsString(payload);

      Map<String, String> fields = Map.of(
          "messageType", messageType,
          "payload", payloadJson,
          "destination", destination != null ? destination : "",
          "targetUsername", targetUsername != null ? targetUsername : "",
          "timestamp", String.valueOf(System.currentTimeMillis())
      );

      stringRedisTemplate.opsForStream().add(streamKey, fields);

      log.debug("Redis Streams 발행 성공: type={}, destination={}, target={}",
          messageType, destination, targetUsername);

    } catch (Exception e) {
      log.error("Redis Streams 발행 실패: type={}, destination={}, error={}",
          messageType, destination, e.getMessage(), e);
      throw new RuntimeException("메시지 브로드캐스트 실패: " + messageType, e);
    }
  }
}