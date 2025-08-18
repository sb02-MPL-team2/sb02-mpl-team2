package com.codeit.sb02mplteam2.domain.livewatch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
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
import com.codeit.sb02mplteam2.exception.livewatch.LiveWatchException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class BasicLiveWatchServiceTest {

    @InjectMocks
    private BasicLiveWatchService liveWatchService;

    @Mock
    private LiveWatchRoomRepository roomRepository;
    
    @Mock
    private LiveWatchParticipantRepository participantRepository;
    
    @Mock
    private LiveWatchMessageRepository messageRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ContentRepository contentRepository;
    
    @Mock
    private LiveWatchBroadcastService broadcastService;

    private User mockUser;
    private Content mockContent;
    private LiveWatchRoom mockRoom;
    private LiveWatchParticipant mockParticipant;
    private LiveWatchMessage mockMessage;
    
    private Long userId;
    private Long contentId;
    private Long roomId;
    private String roomTitle;

    @BeforeEach
    void setUp() {
        userId = 1L;
        contentId = 100L;
        roomId = 10L;
        roomTitle = "테스트 채팅방";

        mockUser = new User("testUser", "test@test.com", "password", null);
        ReflectionTestUtils.setField(mockUser, "id", userId);
        
        mockContent = new Content("테스트 콘텐츠", ContentCategory.MOVIE);
        ReflectionTestUtils.setField(mockContent, "id", contentId);
        
        mockRoom = LiveWatchRoom.builder()
            .content(mockContent)
            .user(null)  // user는 null
            .title(roomTitle)
            .build();
        ReflectionTestUtils.setField(mockRoom, "id", roomId);
        mockParticipant = LiveWatchParticipant.builder()
            .liveWatchRoom(mockRoom)
            .user(mockUser)
            .build();
        mockMessage = LiveWatchMessage.builder()
            .liveWatchRoom(mockRoom)
            .user(mockUser)
            .content("테스트 메시지")
            .messageType(MessageType.CHAT)
            .build();
    }

    @Test
    @DisplayName("콘텐츠가 존재할 때 채팅방이 성공적으로 생성되어야 한다")
    void createRoom_Success() {
        // given
        given(contentRepository.findById(contentId)).willReturn(Optional.of(mockContent));
        given(roomRepository.save(any(LiveWatchRoom.class))).willReturn(mockRoom);

        // when
        LiveWatchRoom actualRoom = liveWatchService.createRoom(contentId, roomTitle);

        // then
        assertThat(actualRoom).isNotNull();
        assertThat(actualRoom.getTitle()).isEqualTo(roomTitle);
        assertThat(actualRoom.getContent()).isEqualTo(mockContent);
        assertThat(actualRoom.getUser()).isNull();

        verify(contentRepository).findById(contentId);
        verify(roomRepository).save(any(LiveWatchRoom.class));
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠로 채팅방 생성 시 예외가 발생해야 한다")
    void createRoom_Fail_ContentNotFound() {
        // given
        given(contentRepository.findById(contentId)).willReturn(Optional.empty());

        // when & then
        assertThrows(LiveWatchException.class, 
            () -> liveWatchService.createRoom(contentId, roomTitle));

        verify(contentRepository).findById(contentId);
        verify(roomRepository, never()).save(any(LiveWatchRoom.class));
    }

    @Test
    @DisplayName("참여자가 메시지를 전송할 때 성공적으로 저장되고 브로드캐스트되어야 한다")
    void sendMessage_Success() {
        // given
        SendMessageRequest request = new SendMessageRequest(roomId, "안녕하세요");
        given(participantRepository.findByLiveWatchRoomIdAndUserIdWithFetchJoins(roomId, userId))
            .willReturn(Optional.of(mockParticipant));
        given(messageRepository.save(any(LiveWatchMessage.class))).willReturn(mockMessage);

        // when
        liveWatchService.sendMessage(request, userId);

        // then
        verify(participantRepository).findByLiveWatchRoomIdAndUserIdWithFetchJoins(roomId, userId);
        verify(messageRepository).save(any(LiveWatchMessage.class));
        verify(broadcastService).broadcastMessage(eq(roomId), any(ChatMessageDto.class));
    }

    @Test
    @DisplayName("참여하지 않은 사용자가 메시지 전송 시 예외가 발생해야 한다")
    void sendMessage_Fail_UserNotInRoom() {
        // given
        SendMessageRequest request = new SendMessageRequest(roomId, "안녕하세요");
        given(participantRepository.findByLiveWatchRoomIdAndUserIdWithFetchJoins(roomId, userId))
            .willReturn(Optional.empty());

        // when & then
        assertThrows(LiveWatchException.class, 
            () -> liveWatchService.sendMessage(request, userId));

        verify(participantRepository).findByLiveWatchRoomIdAndUserIdWithFetchJoins(roomId, userId);
        verify(messageRepository, never()).save(any(LiveWatchMessage.class));
        verify(broadcastService, never()).broadcastMessage(any(), any());
    }

    @Test
    @DisplayName("신규 사용자가 방에 입장할 때 성공적으로 처리되어야 한다")
    void joinAndGetRoomInfo_Success_NewParticipant() {
        // given
        given(roomRepository.findById(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(participantRepository.existsByLiveWatchRoomIdAndUserId(roomId, userId)).willReturn(false);
        given(participantRepository.findFirstByUserId(userId)).willReturn(Optional.empty());
        given(participantRepository.findByLiveWatchRoomIdWithUserFetchJoin(roomId))
            .willReturn(List.of(mockParticipant));

        // when
        RoomJoinResponse response = liveWatchService.joinAndGetRoomInfo(roomId, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.roomId()).isEqualTo(roomId);
        assertThat(response.title()).isEqualTo(roomTitle);
        assertThat(response.participantCount()).isEqualTo(1);
        
        verify(participantRepository).save(any(LiveWatchParticipant.class));
        verify(broadcastService).broadcastParticipantEvent(eq(roomId), any(ChatMessageDto.class));
    }

    @Test
    @DisplayName("이미 참여중인 사용자가 재입장할 때 중복 처리되지 않아야 한다")
    void joinAndGetRoomInfo_Success_AlreadyParticipating() {
        // given
        given(roomRepository.findById(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(participantRepository.existsByLiveWatchRoomIdAndUserId(roomId, userId)).willReturn(true);
        given(participantRepository.findByLiveWatchRoomIdWithUserFetchJoin(roomId))
            .willReturn(List.of(mockParticipant));

        // when
        RoomJoinResponse response = liveWatchService.joinAndGetRoomInfo(roomId, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.roomId()).isEqualTo(roomId);
        
        verify(participantRepository, never()).save(any(LiveWatchParticipant.class));
        verify(broadcastService, never()).broadcastParticipantEvent(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 방에 입장 시 예외가 발생해야 한다")
    void joinAndGetRoomInfo_Fail_RoomNotFound() {
        // given
        given(roomRepository.findById(roomId)).willReturn(Optional.empty());

        // when & then
        assertThrows(LiveWatchException.class, 
            () -> liveWatchService.joinAndGetRoomInfo(roomId, userId));

        verify(roomRepository).findById(roomId);
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 방에 입장 시 예외가 발생해야 한다")
    void joinAndGetRoomInfo_Fail_UserNotFound() {
        // given
        given(roomRepository.findById(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(LiveWatchException.class, 
            () -> liveWatchService.joinAndGetRoomInfo(roomId, userId));

        verify(roomRepository).findById(roomId);
        verify(userRepository).findById(userId);
        verify(participantRepository, never()).save(any());
    }

    @Test
    @DisplayName("다른 방에 참여중인 사용자가 새 방에 입장할 때 자동으로 이동되어야 한다")
    void joinAndGetRoomInfo_Success_MoveFromAnotherRoom() {
        // given
        Long otherRoomId = 99L;
        LiveWatchRoom otherRoom = LiveWatchRoom.builder()
            .content(mockContent)
            .user(null)
            .title("다른 채팅방")
            .build();
        ReflectionTestUtils.setField(otherRoom, "id", otherRoomId);
        
        LiveWatchParticipant existingParticipant = LiveWatchParticipant.builder()
            .liveWatchRoom(otherRoom)
            .user(mockUser)
            .build();

        given(roomRepository.findById(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(participantRepository.existsByLiveWatchRoomIdAndUserId(roomId, userId)).willReturn(false);
        given(participantRepository.findFirstByUserId(userId)).willReturn(Optional.of(existingParticipant));
        given(participantRepository.findByLiveWatchRoomIdWithUserFetchJoin(roomId))
            .willReturn(List.of(mockParticipant));

        // when
        RoomJoinResponse response = liveWatchService.joinAndGetRoomInfo(roomId, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.roomId()).isEqualTo(roomId);
        
        verify(participantRepository).deleteByLiveWatchRoomIdAndUserId(otherRoomId, userId); // 이전 방 퇴장
        verify(participantRepository).save(any(LiveWatchParticipant.class)); // 새 방 입장
    }

    @Test
    @DisplayName("사용자가 방을 퇴장할 때 성공적으로 처리되어야 한다")
    void leaveRoom_Success() {
        // given
        given(roomRepository.findById(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        liveWatchService.leaveRoom(roomId, userId);

        // then
        verify(participantRepository).deleteByLiveWatchRoomIdAndUserId(roomId, userId);
    }

    @Test
    @DisplayName("참여중인 사용자의 연결이 해제될 때 정리 처리되어야 한다")
    void handleUserDisconnect_Success() {
        // given
        given(participantRepository.findFirstByUserId(userId)).willReturn(Optional.of(mockParticipant));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        liveWatchService.handleUserDisconnect(userId);

        // then
        verify(participantRepository).deleteByLiveWatchRoomIdAndUserId(roomId, userId);
    }

    @Test
    @DisplayName("참여중인 방이 없는 사용자의 연결 해제 시 아무 처리도 하지 않아야 한다")
    void handleUserDisconnect_Success_NoParticipatingRoom() {
        // given
        given(participantRepository.findFirstByUserId(userId)).willReturn(Optional.empty());

        // when
        liveWatchService.handleUserDisconnect(userId);

        // then
        verify(participantRepository, never()).deleteByLiveWatchRoomIdAndUserId(any(), any());
    }
}