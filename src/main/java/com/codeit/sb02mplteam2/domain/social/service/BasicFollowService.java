package com.codeit.sb02mplteam2.domain.social.service;

import static com.codeit.sb02mplteam2.domain.notification.entity.NotificationType.NEW_FOLLOWER;

import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.follow.FollowException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    return new FollowResponse(followeeId, followerId);
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
