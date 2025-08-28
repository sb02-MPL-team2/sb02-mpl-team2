package com.codeit.sb02mplteam2.domain.social;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageWsResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageChannelRepository;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageRepository;
import com.codeit.sb02mplteam2.domain.social.service.BasicDirectMessageService;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageChannelException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class BasicDirectMessageServiceTest {

  @InjectMocks
  private BasicDirectMessageService directMessageService;

  @Mock
  private DirectMessageRepository directMessageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private DirectMessageChannelRepository directMessageChannelRepository;
  @Mock
  private SimpMessagingTemplate messagingTemplate;

  private User sender;
  private DirectMessageChannel channel;

  private final Long senderId = 1L;
  private final Long channelId = 10L;

  @BeforeEach
  void setUp() {
    sender = new User("sender", "sender@test.com", "pw", null);
    User receiver = new User("receiver", "receiver@test.com", "pw", null);
    ReflectionTestUtils.setField(sender, "id", senderId);
    Long receiverId = 2L;
    ReflectionTestUtils.setField(receiver, "id", receiverId);

    channel = DirectMessageChannel.of(sender, receiver);
    ReflectionTestUtils.setField(channel, "id", channelId);
  }

  @Test
  @DisplayName("디엠 생성 성공 - 발신자가 채널 소속 유저일 때")
  void create_Success() {
    // given
    DirectMessageCreateRequest request = new DirectMessageCreateRequest(senderId, channelId, "안녕?");
    DirectMessage savedMessage = DirectMessage.of("안녕?", null, sender, channel);
    ReflectionTestUtils.setField(savedMessage, "id", 100L);

    when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
    when(directMessageChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(directMessageRepository.save(any(DirectMessage.class))).thenReturn(savedMessage);

    // when
    DirectMessageResponse response = directMessageService.create(request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.senderId()).isEqualTo(senderId);
    assertThat(response.content()).isEqualTo("안녕?");
    assertThat(response.channelId()).isEqualTo(channelId);

    verify(directMessageRepository).save(any(DirectMessage.class));
    verify(messagingTemplate, times(2))
        .convertAndSendToUser(anyString(), eq("/queue/dm/messages"), any(DirectMessageWsResponse.class));
  }

  @Test
  @DisplayName("디엠 생성 실패 - 존재하지 않는 발신자")
  void create_Fail_UserNotFound() {
    // given
    DirectMessageCreateRequest request = new DirectMessageCreateRequest(senderId, channelId, "안녕?");
    when(userRepository.findById(senderId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(UserException.class, () -> directMessageService.create(request));

    verify(userRepository).findById(senderId);
    verify(directMessageRepository, never()).save(any());
  }

  @Test
  @DisplayName("디엠 생성 실패 - 존재하지 않는 채널")
  void create_Fail_ChannelNotFound() {
    // given
    DirectMessageCreateRequest request = new DirectMessageCreateRequest(senderId, channelId, "안녕?");
    when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
    when(directMessageChannelRepository.findById(channelId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(DirectMessageChannelException.class, () -> directMessageService.create(request));

    verify(userRepository).findById(senderId);
    verify(directMessageChannelRepository).findById(channelId);
    verify(directMessageRepository, never()).save(any());
  }

  @Test
  @DisplayName("디엠 생성 실패 - 발신자가 채널 소속 유저가 아님")
  void create_Fail_InvalidSender() {
    // given
    User stranger = new User("stranger", "stranger@test.com", "pw", null);
    ReflectionTestUtils.setField(stranger, "id", 99L);

    DirectMessageCreateRequest request = new DirectMessageCreateRequest(stranger.getId(), channelId, "해킹?");
    when(userRepository.findById(stranger.getId())).thenReturn(Optional.of(stranger));
    when(directMessageChannelRepository.findById(channelId)).thenReturn(Optional.of(channel));

    // when & then
    assertThrows(IllegalArgumentException.class, () -> directMessageService.create(request));

    verify(directMessageRepository, never()).save(any());
    verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
  }
}
