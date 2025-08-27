package com.codeit.sb02mplteam2.domain.livewatch.redis;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import java.util.Map;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamsConfig {

  private final RedisConnectionFactory connectionFactory;

  @Value("${livewatch.redis.streams.stream-key:livewatch:broadcast}")
  private String streamKey;

  @Value("${livewatch.redis.streams.consumer-id:}")
  private String configuredConsumerId;

  @Bean
  public String consumerId() {
    if (!configuredConsumerId.isEmpty()) {
      log.info("LIVEWATCH_REDIS_STREAMS_CONSUMER_ID 사용: consumer-id={}", configuredConsumerId);
      return configuredConsumerId;
    }

    String randomId = "dev-" + UUID.randomUUID().toString().substring(0, 8);
    log.warn("LIVEWATCH_REDIS_STREAMS_CONSUMER_ID가 설정되지 않아 개발용 ID 생성: consumer-id={}", randomId);
    log.warn("운영 환경에서는 반드시 LIVEWATCH_REDIS_STREAMS_CONSUMER_ID 환경변수를 설정하세요!");
    return randomId;
  }


  @Bean
  public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer(
      RedisStreamsMessageProcessor processor,
      String consumerId,
      String consumerGroupName) {

    ensureConsumerGroup(consumerGroupName);

    StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = 
        StreamMessageListenerContainerOptions
            .<String, MapRecord<String, String, String>>builder()
            .batchSize(10)
            .pollTimeout(Duration.ofMillis(100))
            .build();

    StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = 
        StreamMessageListenerContainer.create(connectionFactory, options);

    String consumerName = getConsumerName(consumerId);

    container.receive(
        Consumer.from(consumerGroupName, consumerName),
        StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
        processor::processMessage
    );

    container.start();

    log.info("Redis Streams Consumer 시작: stream={}, group={}, consumer={}",
        streamKey, consumerGroupName, consumerName);

    return container;
  }

  @Bean
  public String consumerGroupName(String consumerId) {
    return "livewatch-" + consumerId;
  }

  private String getConsumerId() {
    if (!configuredConsumerId.isEmpty()) {
      return configuredConsumerId;
    }
    return "dev-" + UUID.randomUUID().toString().substring(0, 8);
  }

  private String getConsumerName(String consumerId) {
    return consumerId + "-consumer";
  }

  private void ensureConsumerGroup(String consumerGroup) {
    try {
      StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
      
      template.execute((RedisCallback<String>) connection -> {
        return connection.streamCommands().xGroupCreate(
            streamKey.getBytes(StandardCharsets.UTF_8),
            consumerGroup, 
            ReadOffset.from("$"), 
            true
        );
      });
      
      log.info("Consumer Group 생성 성공 (MKSTREAM): group={}, stream={}", consumerGroup, streamKey);
      
    } catch (Exception e) {
      if (e.getMessage() != null && e.getMessage().contains("BUSYGROUP")) {
        log.debug("Consumer Group {} 이미 존재함 (정상)", consumerGroup);
      } else {
        log.error("Consumer Group {} 생성 실패: {}", consumerGroup, e.getMessage());
        // TODO: 실제 문제인 경우 추가 처리 고려
        // - Redis 연결 실패 시 재시도 로직
        // - 권한 오류 시 알림 또는 fallback 처리  
        // - 모니터링/알람 시스템 연동
      }
    }
  }
}