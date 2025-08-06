package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  // follower 계산
  int countByToUser(User toUser);

  // following 계산
  int countByFromUser(User fromUser);

  boolean existsByToUserIdAndFromUserId(Long followeeId, Long followerId);

  Optional<Follow> findByToUserIdAndFromUserId(Long followeeId, Long followerId);
}
