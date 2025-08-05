package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageChannelRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.user.UserException;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  public CursorPageResponseDirectMessageChannelDto findAll(Long userId, LocalDateTime cursor,
      int size) {

    Pageable pageable = PageRequest.of(0, size + 1);
    List<DirectMessageChannel> channels =
        directMessageChannelRepository.findAllByUserIdWithCursor(userId, cursor, pageable);

    boolean hasNext = channels.size() > size;

    if (hasNext) {
      channels = channels.subList(0, size);
    }

    LocalDateTime nextCursor = hasNext ? channels.get(channels.size() - 1).getCreatedAt() : null;

    List<DirectMessageChannelResponse> items = channels.stream()
        .map(c -> {
          User otherUser = c.getFromUser().getId().equals(userId) ? c.getToUser() : c.getFromUser();
          return new DirectMessageChannelResponse(
              new UserSlimDto(otherUser.getId(), null, otherUser.getUsername()), //TODO:user 프로필
              c.getId(),
              userId
          );
        })
        .toList();

    return new CursorPageResponseDirectMessageChannelDto(items, nextCursor, hasNext);
  }

}
