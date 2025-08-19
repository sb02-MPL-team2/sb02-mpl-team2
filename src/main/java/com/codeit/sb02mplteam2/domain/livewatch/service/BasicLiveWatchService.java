package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ParticipantResponseDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchMessage;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchParticipant;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchRoom;
import com.codeit.sb02mplteam2.domain.livewatch.entity.MessageType;
import com.codeit.sb02mplteam2.domain.livewatch.repository.LiveWatchMessageRepository;
import com.codeit.sb02mplteam2.domain.livewatch.repository.LiveWatchParticipantRepository;
import com.codeit.sb02mplteam2.domain.livewatch.repository.LiveWatchRoomRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.livewatch.LiveWatchException;
import java.time.Instant;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  private final LiveWatchParticipantRepository participantRepository;
  private final LiveWatchMessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ContentRepository contentRepository;
  private final LiveWatchBroadcastService broadcastService;

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
    LiveWatchParticipant participant = participantRepository
        .findByLiveWatchRoomIdAndUserIdWithFetchJoins(request.liveWatchRoomId(), userId)
        .orElseThrow(() -> new LiveWatchException(ErrorCode.LIVE_WATCH_USER_NOT_IN_ROOM));

    LiveWatchRoom room = participant.getLiveWatchRoom();
    User user = participant.getUser();

    LiveWatchMessage message = LiveWatchMessage.builder()
        .liveWatchRoom(room)
        .user(user)
        .content(request.content())
        .messageType(MessageType.CHAT)
        .build();

    messageRepository.save(message);

    ChatMessageDto dto = new ChatMessageDto(
        message.getId(),
        message.getContent(),
        message.getSentAt(),
        user.getId(),
        user.getUsername(),
        MessageType.CHAT
    );

    broadcastService.broadcastMessage(request.liveWatchRoomId(), dto);
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
    // TODO: @Cacheable(value = "participantCount", key = "#roomId") 캐시 적용 고려
    return participantRepository.countByLiveWatchRoomId(roomId);
  }

  private List<ParticipantResponseDto> getParticipants(Long roomId) {
    List<LiveWatchParticipant> participants = participantRepository.findByLiveWatchRoomIdWithUserFetchJoin(roomId);

    return participants.stream()
        .map(p -> new ParticipantResponseDto(
            p.getUser().getId(),
            p.getUser().getUsername(),
            p.getUser().getProfile() != null ? p.getUser().getProfile().getUrl() : null,
            p.getParticipatedAt()
        ))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void removeParticipantFromRoom(Long roomId, Long userId) {
    // TODO: @CacheEvict(value = "participantCount", key = "#roomId") 캐시 무효화 적용 고려
    participantRepository.deleteByLiveWatchRoomIdAndUserId(roomId, userId);
  }

  @Override
  @Transactional
  public void handleUserDisconnect(Long userId) {
    if (userId == null) {
      log.warn("handleUserDisconnect 호출 시 userId가 null입니다");
      return;
    }

    try {
      Optional<LiveWatchParticipant> participantOpt = participantRepository.findFirstByUserId(
          userId);

      if (participantOpt.isEmpty()) {
        log.info("사용자 {}는 참여 중인 채팅방이 없습니다", userId);
        return;
      }

      User user = getValidatedUser(userId);

      LiveWatchParticipant participant = participantOpt.get();
      Long roomId = participant.getLiveWatchRoom().getId();

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
    return participantRepository.existsByLiveWatchRoomIdAndUserId(roomId, userId);
  }

  private void leaveOtherRooms(Long userId) {
    Optional<LiveWatchParticipant> participantOpt = participantRepository.findFirstByUserId(userId);

    if (participantOpt.isPresent()) {
      User user = getValidatedUser(userId);
      LiveWatchParticipant participant = participantOpt.get();
      Long existingRoomId = participant.getLiveWatchRoom().getId();

      processLeaveRoom(existingRoomId, user);
    }
  }

  private void addParticipantToRoom(LiveWatchRoom room, User user) {
    // TODO: @CacheEvict(value = "participantCount", key = "#room.id") 캐시 무효화 적용 고려
    LiveWatchParticipant participant = LiveWatchParticipant.builder()
        .liveWatchRoom(room)
        .user(user)
        .participatedAt(LocalDateTime.now())
        .build();

    participantRepository.save(participant);
  }

  private void broadcastJoinMessage(Long roomId, User user) {
    ChatMessageDto joinMessage = new ChatMessageDto(
        null,
        user.getUsername() + "님이 입장했습니다.",
        LocalDateTime.now(),
        user.getId(),
        user.getUsername(),
        MessageType.JOIN
    );

    broadcastService.broadcastParticipantEvent(roomId, joinMessage);
    broadcastService.broadcastMessage(roomId, joinMessage);
  }

  private void processLeaveRoom(Long roomId, User user) {
    removeParticipantFromRoom(roomId, user.getId());

    ChatMessageDto leaveMessage = createLeaveMessage(user);
    broadcastService.broadcastParticipantEvent(roomId, leaveMessage);
    broadcastService.broadcastMessage(roomId, leaveMessage);
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
    addParticipantToRoom(room, user);
    broadcastJoinMessage(roomId, user);
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
