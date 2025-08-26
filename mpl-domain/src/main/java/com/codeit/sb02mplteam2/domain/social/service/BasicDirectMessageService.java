package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageWsResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageChannelRepository;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageChannelException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicDirectMessageService implements DirectMessageService {

  private final DirectMessageRepository directMessageRepository;
  private final UserRepository userRepository;
  private final DirectMessageChannelRepository directMessageChannelRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Transactional
  @Override
  public DirectMessageResponse create(DirectMessageCreateRequest request) {
    // 디엠 보낼 때 이미 채널이 미리 만들어져 있고 디엠쪽에서는 채널이 있는지만 검사(dto에서도 채널아이디 받아옴)
    User sender = userRepository.findById(request.senderId())
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    DirectMessageChannel channel = directMessageChannelRepository.findById(request.channelId())
        .orElseThrow(
            () -> new DirectMessageChannelException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND));

    if (!channel.getFromUser().equals(sender) && !channel.getToUser().equals(sender)) {
      throw new IllegalArgumentException();
    }

    User receiver = channel.getToUser().equals(sender)
        ? channel.getFromUser()
        : channel.getToUser();

    DirectMessage directMessage = DirectMessage.of(request.content(), sender, channel);
    directMessage = directMessageRepository.save(directMessage);

    DirectMessageResponse response = new DirectMessageResponse(
        new UserSlimDto(sender.getId(), null, sender.getUsername()), //TODO:유저프로필
        directMessage.getId(),
        channel.getId(),
        request.content(),
        directMessage.getCreatedAt()
    );

    DirectMessageWsResponse wsMessage = new DirectMessageWsResponse(response, receiver.getId());

    messagingTemplate.convertAndSendToUser(
        receiver.getEmail(),
        "/queue/dm/messages",
        wsMessage
    );

    messagingTemplate.convertAndSendToUser(
        sender.getEmail(),
        "/queue/dm/messages",
        wsMessage
    );

    log.info("[WebSocket DM] sender: {}, receiver: {}, content: {}",
        sender.getId(), receiver.getId(), response.content());

    return response;
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseDirectMessageDto findAll(Long channelId, Long after, Long before, int limit){
    List<DirectMessage> messages;

    if (after != null) {
      messages = directMessageRepository.findMessagesAfter(channelId, after, PageRequest.of(0, limit + 1));
    } else if (before != null) {
      messages = directMessageRepository.findMessagesBefore(channelId, before, PageRequest.of(0, limit + 1));
    } else {
      messages = directMessageRepository.findInitialMessages(channelId, PageRequest.of(0, limit));
      Collections.reverse(messages);
    }

    boolean hasNext = messages.size() > limit;
    if (hasNext) {
      messages = messages.subList(0, limit);
    }

    //before 조회 시 역순 정렬
    if (before != null) {
      Collections.reverse(messages);
    }

    List<DirectMessageResponse> items = messages.stream()
        .map(m -> new DirectMessageResponse(
            new UserSlimDto(m.getUser().getId(), null, m.getUser().getUsername()),
            m.getId(),
            m.getDirectMessageChannel().getId(),
            m.getContent(),
            m.getCreatedAt()
        ))
        .toList();

    String startCursor = items.isEmpty() ? null : String.valueOf(items.get(0).directMessageId());
    String endCursor = items.isEmpty() ? null : String.valueOf(items.get(items.size() - 1).directMessageId());

    return new CursorPageResponseDirectMessageDto(
        items,
        hasNext,
        (after != null || before != null), //hasPrevious
        startCursor,
        endCursor
    );
  }

  @Override
  public Long findReceiverId(Long channelId, Long senderId) {
    DirectMessageChannel channel = directMessageChannelRepository.findById(channelId)
        .orElseThrow(() -> new DirectMessageChannelException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND));

    if (channel.getFromUser().getId().equals(senderId)) return channel.getToUser().getId();
    if (channel.getToUser().getId().equals(senderId)) return channel.getFromUser().getId();

    throw new DirectMessageChannelException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND);
  }
}
