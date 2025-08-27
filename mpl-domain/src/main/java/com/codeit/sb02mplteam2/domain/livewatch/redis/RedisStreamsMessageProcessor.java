package com.codeit.sb02mplteam2.domain.livewatch.redis;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamsMessageProcessor {

  private final SimpMessagingTemplate messagingTemplate;
  private final ObjectMapper objectMapper;
  private final StringRedisTemplate stringRedisTemplate;

  @Value("${livewatch.redis.streams.stream-key:livewatch:broadcast}")
  private String streamKey;

  private final String consumerGroupName;

  public void processMessage(MapRecord<String, String, String> record) {
    try {
      Map<String, String> value = record.getValue();
      String messageType = value.get("messageType");
      String payloadJson = value.get("payload");
      String destination = value.get("destination");
      String targetUsername = value.get("targetUsername");

      log.debug("Redis Streams 메시지 수신: type={}, recordId={}", messageType, record.getId());

      switch (messageType) {
        case "CHAT_MESSAGE" -> processChatMessage(destination, payloadJson);
        case "PARTICIPANT_EVENT" -> processParticipantEvent(destination, payloadJson);
        case "ERROR" -> processErrorMessage(targetUsername, payloadJson);
        case "SYSTEM" -> processSystemMessage(destination, payloadJson);
        default -> log.warn("알 수 없는 메시지 타입: type={}, recordId={}", messageType, record.getId());
      }

    } catch (Exception e) {
      log.error("메시지 처리 실패: recordId={}, error={}", record.getId(), e.getMessage(), e);
    } finally {
      acknowledge(record.getId());
    }
  }

  private void processChatMessage(String destination, String payloadJson) {
    try {
      ChatMessageDto message = objectMapper.readValue(payloadJson, ChatMessageDto.class);
      messagingTemplate.convertAndSend(destination, message);

      log.debug("채팅 메시지 브로드캐스트 완료: destination={}, userId={}",
          destination, message.userId());

    } catch (Exception e) {
      log.error("채팅 메시지 처리 실패: destination={}, error={}", destination, e.getMessage());
      throw new RuntimeException("채팅 메시지 처리 실패", e);
    }
  }

  private void processParticipantEvent(String destination, String payloadJson) {
    try {
      ChatMessageDto eventMessage = objectMapper.readValue(payloadJson, ChatMessageDto.class);
      messagingTemplate.convertAndSend(destination, eventMessage);

      log.debug("참가자 이벤트 브로드캐스트 완료: destination={}, type={}",
          destination, eventMessage.messageType());

    } catch (Exception e) {
      log.error("참가자 이벤트 처리 실패: destination={}, error={}", destination, e.getMessage());
      throw new RuntimeException("참가자 이벤트 처리 실패", e);
    }
  }

  private void processErrorMessage(String targetUsername, String payloadJson) {
    try {
      if (targetUsername == null || targetUsername.isEmpty()) {
        log.warn("에러 메시지의 targetUsername이 비어있음");
        return;
      }

      ErrorNotificationDto error = objectMapper.readValue(payloadJson, ErrorNotificationDto.class);
      messagingTemplate.convertAndSendToUser(targetUsername, "/queue/errors", error);

      log.debug("에러 메시지 전송 완료: user={}, errorCode={}",
          targetUsername, error.errorCode());

    } catch (Exception e) {
      log.error("에러 메시지 처리 실패: user={}, error={}", targetUsername, e.getMessage());
      throw new RuntimeException("에러 메시지 처리 실패", e);
    }
  }

  private void processSystemMessage(String destination, String payloadJson) {
    try {
      String systemMessage = objectMapper.readValue(payloadJson, String.class);
      messagingTemplate.convertAndSend(destination, systemMessage);

      log.debug("시스템 메시지 브로드캐스트 완료: destination={}", destination);

    } catch (Exception e) {
      log.error("시스템 메시지 처리 실패: destination={}, error={}", destination, e.getMessage());
      throw new RuntimeException("시스템 메시지 처리 실패", e);
    }
  }

  private void acknowledge(RecordId recordId) {
    try {
      stringRedisTemplate.opsForStream()
          .acknowledge(streamKey, consumerGroupName, recordId);

      log.debug("메시지 ACK 완료: recordId={}, group={}", recordId, consumerGroupName);

    } catch (Exception e) {
      log.error("메시지 ACK 실패 - PEL에 누적될 수 있음: recordId={}, group={}, error={}", 
          recordId, consumerGroupName, e.getMessage());
    }
  }
}