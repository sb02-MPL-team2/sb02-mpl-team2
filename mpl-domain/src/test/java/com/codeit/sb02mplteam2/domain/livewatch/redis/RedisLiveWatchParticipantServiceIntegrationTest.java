package com.codeit.sb02mplteam2.domain.livewatch.redis;

import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ParticipantResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RedisLiveWatchParticipantServiceIntegrationTest {

  @Container
  static GenericContainer<?> redis = new GenericContainer<>("redis:7.0-alpine")
      .withExposedPorts(6379);

  @Autowired
  private RedisLiveWatchParticipantService participantService;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
  }

  @AfterEach
  void tearDown() {
    stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  @Test
  @DisplayName("사용자가 새로운 방에 입장할 때 기존 방에서 자동으로 퇴장되어야 한다")
  void joinRoom_Success_AutoLeaveFromPreviousRoom() {
    // given
    Long previousRoomId = 200L;
    Long newRoomId = 300L;
    Long userId = 1001L;

    participantService.joinRoom(previousRoomId, userId, "testUser", "profile.jpg");

    // when
    participantService.joinRoom(newRoomId, userId, "testUser", "profile.jpg");

    // then
    assertThat(participantService.getCurrentRoom(userId)).isEqualTo(newRoomId);
    assertThat(participantService.isAlreadyParticipating(previousRoomId, userId)).isFalse();
    assertThat(participantService.isAlreadyParticipating(newRoomId, userId)).isTrue();
  }

  @Test
  @DisplayName("잘못된 필드 데이터가 있을 때 DTO 변환 실패 시 해당 참가자를 제외하고 반환되어야 한다")
  void getParticipants_Success_FilterOutInvalidData() {
    // given
    Long roomId = 100L;
    String participantsKey = "livewatch:room:" + roomId + ":participants";

    // 정상 데이터 삽입
    participantService.joinRoom(roomId, 1001L, "validUser", "profile.jpg");

    // 잘못된 데이터 직접 삽입 (participatedAt 필드 누락)
    stringRedisTemplate.opsForHash().put(participantsKey, "userId:1002:userId", "1002");
    stringRedisTemplate.opsForHash().put(participantsKey, "userId:1002:userName", "invalidUser");
    stringRedisTemplate.opsForHash().put(participantsKey, "userId:1002:profileUrl", "invalid.jpg");

    // when
    List<ParticipantResponseDto> participants = participantService.getParticipants(roomId);

    // then
    assertThat(participants).hasSize(1);
    assertThat(participants.get(0).userName()).isEqualTo("validUser");
  }

  @Test
  @DisplayName("동일한 사용자가 중복 입장할 때 기존 데이터를 덮어써야 한다")
  void joinRoom_Success_OverwriteExistingUser() {
    // given
    Long roomId = 100L;
    Long userId = 1001L;

    participantService.joinRoom(roomId, userId, "originalName", "original.jpg");

    // when
    participantService.joinRoom(roomId, userId, "updatedName", "updated.jpg");

    // then
    List<ParticipantResponseDto> participants = participantService.getParticipants(roomId);
    assertThat(participants).hasSize(1);

    ParticipantResponseDto participant = participants.get(0);
    assertThat(participant.userName()).isEqualTo("updatedName");
    assertThat(participant.profileUrl()).isEqualTo("updated.jpg");
  }

  @Test
  @DisplayName("참가자 수가 정확히 계산되어야 한다")
  void getParticipantCount_Success() {
    // given
    Long roomId = 100L;
    participantService.joinRoom(roomId, 1001L, "user1", "profile1.jpg");
    participantService.joinRoom(roomId, 1002L, "user2", "profile2.jpg");

    // when
    Integer count = participantService.getParticipantCount(roomId);

    // then
    assertThat(count).isEqualTo(2); // 실제 참가자 수
    assertThat(participantService.getParticipants(roomId)).hasSize(2); // 실제 참가자는 2명
  }

  @Test
  @DisplayName("예상치 못한 형식의 필드명이 있을 때 무시하고 처리되어야 한다")
  void getParticipants_Success_IgnoreInvalidFieldFormat() {
    // given
    Long roomId = 100L;
    String participantsKey = "livewatch:room:" + roomId + ":participants";

    // 정상 데이터
    participantService.joinRoom(roomId, 1001L, "validUser", "profile.jpg");

    // 잘못된 형식의 필드명들 직접 삽입
    stringRedisTemplate.opsForHash().put(participantsKey, "invalidField", "value1");
    stringRedisTemplate.opsForHash().put(participantsKey, "userId:123", "value2"); // 필드명 부족
    stringRedisTemplate.opsForHash()
        .put(participantsKey, "userId:123:userName:extra:colon", "value3"); // 콜론 과다

    // when
    List<ParticipantResponseDto> participants = participantService.getParticipants(roomId);

    // then
    assertThat(participants).hasSize(1);
    assertThat(participants.get(0).userName()).isEqualTo("validUser");
  }
}