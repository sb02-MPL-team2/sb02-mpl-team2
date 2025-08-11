package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageChannelRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageChannelException;
import com.codeit.sb02mplteam2.exception.user.UserException;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  @Transactional(readOnly = true)
  @Override
  public DirectMessageChannelResponse findByChannelId(Long channelId, Long userId) {
    DirectMessageChannel channel = directMessageChannelRepository.findById(channelId)
        .orElseThrow(
            () -> new DirectMessageChannelException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND));

    User otherUser;

    if (Objects.equals(channel.getToUser().getId(), userId)) {
      otherUser = channel.getFromUser();
    } else if (Objects.equals(channel.getFromUser().getId(), userId)) {
      otherUser = channel.getToUser();
    } else {
      throw new DirectMessageChannelException(ErrorCode.DIRECT_MESSAGE_CHANNEL_NOT_FOUND);
    }

    return new DirectMessageChannelResponse(
        new UserSlimDto(otherUser.getId(), null, otherUser.getUsername()),
        channelId, //TODO: 유저 프로필
        userId
    );
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseDirectMessageChannelDto findAll(Long userId, Long cursor,
      int size) {
    List<DirectMessageChannel> channels = directMessageChannelRepository.findByUserIdWithCursor(userId, cursor, PageRequest.of(0, size + 1));

    boolean hasNext = channels.size() > size;
    if (hasNext) {
      channels = channels.subList(0, size);
    }

    Long nextCursor = hasNext ? channels.get(channels.size() - 1).getId() : null;

    List<DirectMessageChannelResponse> items = channels.stream()
        .map(ch -> {
          // 상대방 유저 찾기 (채널 당사자 중 userId가 아닌 쪽)
          User otherUser = ch.getFromUser().getId().equals(userId) ? ch.getToUser() : ch.getFromUser();

          UserSlimDto userDto = new UserSlimDto(
              otherUser.getId(),
              null, //TODO:유저프로필
              otherUser.getUsername()
          );

          return new DirectMessageChannelResponse(userDto, ch.getId(), userId);
        })
        .toList();

    return new CursorPageResponseDirectMessageChannelDto(items, nextCursor, hasNext);
  }

}
