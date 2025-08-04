package com.codeit.sb02mplteam2.domain.social.service;

import static com.codeit.sb02mplteam2.domain.notification.entity.NotificationType.NEW_FOLLOWER;

import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseFollowDto;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.follow.FollowException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicFollowService implements FollowService {

  private final UserRepository userRepository;
  private final FollowRepository followRepository;
  private final NotificationService notificationService;

  @Transactional
  @Override
  public FollowResponse create(Long followeeId, Long followerId) {
    if(Objects.equals(followeeId, followerId)){
      throw new IllegalArgumentException();
    }
    if (followRepository.existsByToUserIdAndFromUserId(followeeId, followerId)) {
      throw new IllegalArgumentException(); //예외처리 수정 필요
    }

    User followee = userRepository.findById(followeeId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    Follow follow = Follow.of(follower, followee);
    followRepository.save(follow);

    notificationService.create(followeeId, NEW_FOLLOWER, follow.getId(), followerId);

    followee.increaseFollowerCount();
    follower.increaseFollowingCount();

    return new FollowResponse(followeeId, followerId);
  }

  @Transactional
  @Override
  public FollowResponse delete(Long followeeId, Long followerId){
    if(Objects.equals(followeeId, followerId)){
      throw new IllegalArgumentException();
    }

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
  public CursorPageResponseFollowDto getFollowers(Long userId, LocalDateTime cursor, int size){
    Pageable pageable = PageRequest.of(0, size + 1);

    List<Follow> follows = followRepository.findFollowersWithCursor(userId, cursor, pageable);

    boolean hasNext = follows.size() > size;

    List<Follow> result = hasNext ? follows.subList(0, size) : follows;

    List<UserSlimDto> userList = result.stream()
        .map(f -> {
          User user = f.getFromUser();
          return new UserSlimDto(user.getId(), null, user.getUsername());
        })//TODO: profileUrl 값 수정해야됨
        .toList();

    LocalDateTime nextCursor = hasNext ? result.get(result.size() - 1).getCreatedAt() : null;

    return new CursorPageResponseFollowDto(userList, nextCursor, hasNext);
  }

  @Transactional(readOnly = true)
  @Override
  public CursorPageResponseFollowDto getFollowings(Long userId, LocalDateTime cursor, int size){
    Pageable pageable = PageRequest.of(0, size + 1);

    List<Follow> follows = followRepository.findFollowingsWithCursor(userId, cursor, pageable);

    boolean hasNext = follows.size() > size;

    List<Follow> result = hasNext ? follows.subList(0, size) : follows;

    List<UserSlimDto> userList = result.stream()
        .map(f -> {
          User user = f.getToUser();
          return new UserSlimDto(user.getId(), null, user.getUsername());
        })//TODO: profileUrl 값 수정해야됨
        .toList();

    LocalDateTime nextCursor = hasNext ? result.get(result.size() - 1).getCreatedAt() : null;

    return new CursorPageResponseFollowDto(userList, nextCursor, hasNext);
  }

  @Override
  public FollowStatusResponse isFollowing(Long followeeId, Long followerId){
    if(Objects.equals(followeeId, followerId)){
      throw new IllegalArgumentException();
    }
    if (followRepository.existsByToUserIdAndFromUserId(followeeId, followerId)) {
      return new FollowStatusResponse(true, followeeId, followerId);
    }
    return new FollowStatusResponse(false, followeeId, followerId);
  }


}
