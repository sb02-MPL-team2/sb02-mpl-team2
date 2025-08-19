package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.DirectMessageChannel;
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
        SELECT dmc
        FROM DirectMessageChannel dmc
        JOIN FETCH dmc.fromUser fu
        JOIN FETCH dmc.toUser tu
        WHERE (dmc.fromUser.id = :userId OR dmc.toUser.id = :userId)
        AND (:cursor IS NULL OR dmc.id < :cursor)
        ORDER BY dmc.id DESC
    """)
  List<DirectMessageChannel> findByUserIdWithCursor(
      @Param("userId") Long userId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );
}
