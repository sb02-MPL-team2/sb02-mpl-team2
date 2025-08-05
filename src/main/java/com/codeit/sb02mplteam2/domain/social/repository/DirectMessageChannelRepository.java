package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageChannelRepository extends JpaRepository<DirectMessageChannel, Long> {

  boolean existsByFromUserIdAndToUserIdOrFromUserIdAndToUserId(Long senderId, Long receiverId,
      Long receiverId2, Long senderId2);

  Optional<DirectMessageChannel> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
      Long senderId, Long receiverId,
      Long receiverId2, Long senderId2
  );
}
