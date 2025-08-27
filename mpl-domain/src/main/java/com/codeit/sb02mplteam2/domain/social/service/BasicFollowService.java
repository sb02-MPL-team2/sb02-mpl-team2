package com.codeit.sb02mplteam2.domain.social.service;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseFollowDto;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserFollowDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.follow.FollowException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicFollowService implements FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public FollowResponse create(Long followeeId, Long followerId) {
    if(Objects.equals(followeeId, followerId)){
      throw new FollowException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }
    if (followRepository.existsByToUserIdAndFromUserId(followeeId, followerId)) {
      throw new FollowException(ErrorCode.FOLLOW_ALREADY_EXISTS);
    }

    User followee = userRepository.findById(followeeId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    Follow follow = Follow.of(follower, followee);
    followRepository.save(follow);

    followee.increaseFollowerCount();
    follower.increaseFollowingCount();

    log.info("{}를 {}가 팔로우 ", followee.getUsername(), follower.getUsername());
    eventPublisher.publishEvent(new NotificationEvent(
        followee.getId(),
        NotificationType.NEW_FOLLOWER,
        null,
        follower.getId()
    ));

    return new FollowResponse(followeeId, followerId);
  }

  @Transactional
  @Override
  public FollowResponse delete(Long followeeId, Long followerId){

    User followee = userRepository.findById(followeeId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    Follow follow = followRepository.findByToUserIdAndFromUserId(followeeId, followerId)
        .orElseThrow(() -> new FollowException(ErrorCode.FOLLOW_NOT_FOUND));

    followRepository.delete(follow);

    followee.decreaseFollowerCount();
    follower.decreaseFollowingCount();

    return new FollowResponse(followeeId, followerId);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseFollowDto getFollowers(Long userId, Long cursor, int size){
    List<Follow> follows = followRepository.findFollowers(userId, cursor, PageRequest.of(0, size + 1));

    boolean hasNext = follows.size() > size;
    if (hasNext) {
      follows = follows.subList(0, size);
    }

    Long nextCursor = hasNext
        ? follows.get(follows.size() - 1).getId()
        : null;

    List<UserFollowDto> userList = follows.stream()
        .map(f -> new UserFollowDto(
            f.getFromUser().getId(),
            f.getFromUser().getProfile() != null ? f.getFromUser().getProfile().getUrl() : null,
            f.getFromUser().getUsername(),
            f.getFromUser().getFollowerCount(),
            f.getFromUser().getFollowingCount(),
            f.getFromUser().getRole()
        ))
        .toList();

    return new CursorPageResponseFollowDto(userList, nextCursor, hasNext);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseFollowDto getFollowings(Long userId, Long cursor, int size){
    List<Follow> follows = followRepository.findFollowings(userId, cursor, PageRequest.of(0, size + 1));

    boolean hasNext = follows.size() > size;
    if (hasNext) {
      follows = follows.subList(0, size);
    }

    Long nextCursor = hasNext ? follows.get(follows.size() - 1).getId() : null;

    List<UserFollowDto> userList = follows.stream()
        .map(f -> new UserFollowDto(
            f.getToUser().getId(),
            f.getToUser().getProfile() != null ? f.getToUser().getProfile().getUrl() : null,
            f.getToUser().getUsername(),
            f.getToUser().getFollowerCount(),
            f.getToUser().getFollowingCount(),
            f.getToUser().getRole()
        ))
        .toList();

    return new CursorPageResponseFollowDto(userList, nextCursor, hasNext);
  }

  @Override
  public FollowStatusResponse isFollowing(Long followeeId, Long followerId){
    if (followRepository.existsByToUserIdAndFromUserId(followeeId, followerId)) {
      return new FollowStatusResponse(true, followeeId, followerId);
    }
    return new FollowStatusResponse(false, followeeId, followerId);
  }

}
