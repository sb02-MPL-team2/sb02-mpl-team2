package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ParticipantResponseDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchMessage;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchRoom;
import com.codeit.sb02mplteam2.domain.livewatch.redis.RedisLiveWatchParticipantService;
import com.codeit.sb02mplteam2.domain.livewatch.entity.MessageType;
import com.codeit.sb02mplteam2.domain.livewatch.repository.LiveWatchMessageRepository;
import com.codeit.sb02mplteam2.domain.livewatch.repository.LiveWatchRoomRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.livewatch.LiveWatchException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicLiveWatchService implements LiveWatchService {

  private final LiveWatchRoomRepository roomRepository;
  private final LiveWatchMessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ContentRepository contentRepository;
  private final LiveWatchBroadcastService broadcastService;

  private final RedisLiveWatchParticipantService redisParticipantService;

  @Override
  @Transactional
  public LiveWatchRoom createRoom(Long contentId, String title) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(() -> new LiveWatchException(ErrorCode.CONTENT_NOT_FOUND));

    LiveWatchRoom room = LiveWatchRoom.builder()
        .content(content)
        .user(null)  // 현재 로직 상 채팅방 소유자 없음
        .title(title)
        .build();

    return roomRepository.save(room);
  }

  @Override
  @Transactional
  public RoomJoinResponse getOrCreateRoomByContentAndJoin(Long contentId, Long userId) {
    Content content = contentRepository.findById(contentId)
        .orElseThrow(() -> new LiveWatchException(ErrorCode.CONTENT_NOT_FOUND));

    // 해당 콘텐츠의 기존 방을 찾음
    Optional<LiveWatchRoom> existingRoom = roomRepository.findByContentId(contentId);

    LiveWatchRoom room;
    if (existingRoom.isPresent()) {
      room = existingRoom.get();
    } else {
      // 새 방 생성
      room = LiveWatchRoom.builder()
          .content(content)
          .user(null)  // 현재 로직 상 채팅방 소유자 없음
          .title(content.getTitle() + " 같이보기")
          .build();
      room = roomRepository.save(room);
    }

    // 자동으로 방에 참여
    return joinAndGetRoomInfo(room.getId(), userId);
  }

  @Override
  @Transactional
  public void sendMessage(SendMessageRequest request, Long userId) {
    if (!redisParticipantService.isAlreadyParticipating(request.liveWatchRoomId(), userId)) {
      throw new LiveWatchException(ErrorCode.LIVE_WATCH_USER_NOT_IN_ROOM);
    }

    LiveWatchRoom room = getValidatedRoom(request.liveWatchRoomId());
    User user = getValidatedUser(userId);

    LiveWatchMessage message = LiveWatchMessage.builder()
        .liveWatchRoom(room)
        .user(user)
        .content(request.content())
        .messageType(MessageType.CHAT)
        .build();

    LiveWatchMessage savedMessage = messageRepository.save(message);

    ChatMessageDto dto = new ChatMessageDto(
        savedMessage.getId(),
        savedMessage.getContent(),
        savedMessage.getSentAt(),
        user.getId(),
        user.getUsername(),
        MessageType.CHAT
    );

    try {
      broadcastService.broadcastMessage(request.liveWatchRoomId(), dto);
    } catch (Exception e) {
      log.warn("메시지 브로드캐스트 실패 (RDB는 저장됨): roomId={}, messageId={}",
          request.liveWatchRoomId(), savedMessage.getId(), e);
    }
  }

  @Override
  public ChatMessagePageResponse getMessages(Long roomId, String cursorStr, Integer size) {
    if (!roomRepository.existsById(roomId)) {
      throw new LiveWatchException(ErrorCode.LIVE_WATCH_ROOM_NOT_FOUND);
    }

    int pageSize = size != null ? size : 30;
    PageRequest pageRequest = PageRequest.of(0, pageSize + 1,
        Sort.by(Sort.Direction.DESC, "sentAt")
            .and(Sort.by(Sort.Direction.DESC, "id")));

    List<LiveWatchMessage> messages;
    if (cursorStr != null) {
      ChatCursor cursor = ChatCursor.from(cursorStr);
      messages = messageRepository.findMessagesWithCursor(
          roomId,
          cursor.sentAt(),
          cursor.id(),
          pageRequest
      );
    } else {
      messages = messageRepository.findByLiveWatchRoomId(roomId, pageRequest);
    }

    boolean hasNext = messages.size() > pageSize;
    List<LiveWatchMessage> responseMessages = hasNext ? messages.subList(0, pageSize) : messages;

    List<ChatMessageDto> messageResponses = responseMessages.stream()
        .map(ChatMessageDto::fromEntity)
        .collect(Collectors.toList());

    String nextCursor = null;
    if (hasNext) {
      LiveWatchMessage lastMsg = responseMessages.get(responseMessages.size() - 1);
      long epochMillis = lastMsg.getSentAt()
          .atZone(ZoneOffset.UTC)
          .toInstant()
          .toEpochMilli();
      nextCursor = epochMillis + "_" + lastMsg.getId();
    }

    return new ChatMessagePageResponse(
        messageResponses,
        messageResponses.size(),
        nextCursor,
        hasNext
    );
  }


  @Override
  @Transactional
  public RoomJoinResponse joinAndGetRoomInfo(Long roomId, Long userId) {
    processRoomJoin(roomId, userId);

    LiveWatchRoom room = getValidatedRoom(roomId);

    List<ParticipantResponseDto> participants = getParticipants(roomId);

    return new RoomJoinResponse(
        room.getId(),
        room.getTitle(),
        room.getCreatedAt(),
        participants.size(),
        participants
    );
  }

  @Override
  @Transactional
  public void leaveRoom(Long roomId, Long userId) {
    User user = getValidatedUser(userId);
    getValidatedRoom(roomId);

    processLeaveRoom(roomId, user);
  }

  @Override
  public Integer getParticipantCount(Long roomId) {
    return redisParticipantService.getParticipantCount(roomId);
  }

  private List<ParticipantResponseDto> getParticipants(Long roomId) {
    return redisParticipantService.getParticipants(roomId);
  }

  @Override
  @Transactional
  public void removeParticipantFromRoom(Long roomId, Long userId) {
    try {
      redisParticipantService.leaveRoom(roomId, userId);
    } catch (Exception e) {
      log.warn("Redis 참가자 제거 실패: roomId={}, userId={}", roomId, userId, e);
    }
  }

  @Override
  @Transactional
  public void handleUserDisconnect(Long userId) {
    if (userId == null) {
      log.warn("handleUserDisconnect 호출 시 userId가 null입니다");
      return;
    }

    try {
      Long roomId = redisParticipantService.getCurrentRoom(userId);

      if (roomId == null) {
        log.info("사용자 {}는 참여 중인 채팅방이 없습니다", userId);
        return;
      }

      User user = getValidatedUser(userId);

      try {
        processLeaveRoom(roomId, user);
        log.info("사용자 {} 채팅방 {} 비정상 종료 처리 완료", userId, roomId);
      } catch (Exception e) {
        log.error("사용자 {} 채팅방 {} 비정상 종료 처리 중 오류", userId, roomId, e);
      }
    } catch (Exception e) {
      log.error("사용자 {} 비정상 종료 처리 중 전체 오류", userId, e);
    }
  }

  private boolean isAlreadyParticipating(Long roomId, Long userId) {
    return redisParticipantService.isAlreadyParticipating(roomId, userId);
  }

  private void leaveOtherRooms(Long userId) {
    Long existingRoomId = redisParticipantService.getCurrentRoom(userId);

    if (existingRoomId != null) {
      User user = getValidatedUser(userId);
      // 트랜잭션 내에서 이전 방 퇴장 처리
      processLeaveRoom(existingRoomId, user);
    }
  }

  private void addParticipantToRoom(LiveWatchRoom room, User user) {
    boolean redisSuccess = false;
    try {
      redisParticipantService.joinRoom(
          room.getId(),
          user.getId(),
          user.getUsername(),
          user.getProfile() != null ? user.getProfile().getUrl() : null
      );
      redisSuccess = true;

      broadcastJoinMessage(room.getId(), user);

    } catch (Exception e) {
      log.error("참가자 추가 실패: roomId={}, userId={}", room.getId(), user.getId(), e);

      if (redisSuccess) {
        try {
          redisParticipantService.leaveRoom(room.getId(), user.getId());
          log.info("참가자 추가 실패로 인한 Redis 롤백 완료: roomId={}, userId={}", room.getId(), user.getId());
        } catch (Exception rollbackException) {
          log.error("참가자 추가 롤백 실패: roomId={}, userId={}", room.getId(), user.getId(),
              rollbackException);
        }
      }
      throw e; // 원래 예외 재전파
    }
  }

  private void broadcastJoinMessage(Long roomId, User user) {
    try {
      ChatMessageDto joinMessage = new ChatMessageDto(
          null,
          user.getUsername() + "님이 입장했습니다.",
          LocalDateTime.now(),
          user.getId(),
          user.getUsername(),
          MessageType.JOIN
      );

      broadcastService.broadcastParticipantEvent(roomId, joinMessage);
    } catch (Exception e) {
      log.warn("입장 메시지 브로드캐스트 실패: roomId={}, userId={}", roomId, user.getId(), e);
    }
  }

  private void processLeaveRoom(Long roomId, User user) {
    removeParticipantFromRoom(roomId, user.getId());

    ChatMessageDto leaveMessage = createLeaveMessage(user);

    broadcastService.broadcastParticipantEvent(roomId, leaveMessage);
  }

  private ChatMessageDto createLeaveMessage(User user) {
    return new ChatMessageDto(
        null,
        user.getUsername() + "님이 나갔습니다.",
        LocalDateTime.now(),
        user.getId(),
        user.getUsername(),
        MessageType.LEAVE
    );
  }

  private record ChatCursor(
      Instant sentAt,
      Long id
  ) {

    public static ChatCursor from(String cursorString) {
      if (cursorString == null || cursorString.isBlank()) {
        throw new LiveWatchException(ErrorCode.LIVE_WATCH_MESSAGE_CURSOR_INVALID);
      }

      String[] parts = cursorString.split("_");
      if (parts.length != 2) {
        throw new LiveWatchException(ErrorCode.LIVE_WATCH_MESSAGE_CURSOR_INVALID);
      }

      try {
        Instant sentAt = Instant.ofEpochMilli(Long.parseLong(parts[0]));

        Long id = Long.parseLong(parts[1]);
        return new ChatCursor(sentAt, id);
      } catch (NumberFormatException e) {
        throw new LiveWatchException(ErrorCode.LIVE_WATCH_MESSAGE_CURSOR_INVALID);
      }
    }
  }

  private void processRoomJoin(Long roomId, Long userId) {
    LiveWatchRoom room = getValidatedRoom(roomId);
    User user = getValidatedUser(userId);

    if (isAlreadyParticipating(roomId, userId)) {
      return;
    }

    leaveOtherRooms(userId);
    addParticipantToRoom(room, user); // 이 안에서 브로드캐스트도 처리됨
  }

  private User getValidatedUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new LiveWatchException(ErrorCode.USER_NOT_FOUND));
  }

  private LiveWatchRoom getValidatedRoom(Long roomId) {
    return roomRepository.findById(roomId)
        .orElseThrow(() -> new LiveWatchException(ErrorCode.LIVE_WATCH_ROOM_NOT_FOUND));
  }
}
