package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicDirectMessageService implements DirectMessageService {

  private final DirectMessageRepository directMessageRepository;
  private final UserRepository userRepository;
  private final DirectMessageChannelRepository directMessageChannelRepository;

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

    return new DirectMessageResponse(new UserSlimDto(sender.getId(), null, sender.getUsername()),
        directMessage.getId(), channel.getId(), request.content(), directMessage.getCreatedAt());
  }
}
