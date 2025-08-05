package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageChannelRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageChannelException;
import com.codeit.sb02mplteam2.exception.follow.FollowException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicDirectMessageChannelService implements DirectMessageChannelService {

  private final DirectMessageChannelRepository directMessageChannelRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public DirectMessageChannelResponse create(Long senderId, Long receiverId) {

    User sender = userRepository.findById(senderId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    User receiver = userRepository.findById(receiverId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    DirectMessageChannel channel = directMessageChannelRepository
        .findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
            senderId, receiverId,   // 1 -> 2
            receiverId, senderId    // 2 -> 1
        )
        .orElseGet(() -> {
          DirectMessageChannel newChannel = DirectMessageChannel.of(sender, receiver);
          return directMessageChannelRepository.save(newChannel);
        });

    return new DirectMessageChannelResponse(
        new UserSlimDto(receiverId, null, receiver.getUsername()), // TODO: 프로필 null 수정
        channel.getId(),
        senderId
    );
  }

}
