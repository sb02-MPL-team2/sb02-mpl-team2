package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DirectMessageChannelRepository extends JpaRepository<DirectMessageChannel, Long> {

  Optional<DirectMessageChannel> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
      Long senderId, Long receiverId,
      Long receiverId2, Long senderId2
  );

  @Query("""
        SELECT c
        FROM DirectMessageChannel c
        WHERE (c.fromUser.id = :userId OR c.toUser.id = :userId)
        AND (:cursor IS NULL OR c.createdAt < :cursor)
        ORDER BY c.createdAt DESC
    """)
  List<DirectMessageChannel> findAllByUserIdWithCursor(
      @Param("userId") Long userId,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable
  );
}
