package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {


  @Query("""
        SELECT m
        FROM DirectMessage m
        WHERE m.directMessageChannel.id = :channelId
          AND (:cursor IS NULL OR m.createdAt < :cursor)
        ORDER BY m.createdAt DESC
    """)
  List<DirectMessage> findMessagesByChannelWithCursor(Long channelId, LocalDateTime cursor, Pageable pageable);


  // after → 이후 메시지 조회 (ASC)
  @Query("SELECT dm FROM DirectMessage dm " +
      "JOIN FETCH dm.user s " +
      "JOIN FETCH dm.directMessageChannel c " +
      "WHERE c.id = :channelId AND dm.id > :after " +
      "ORDER BY dm.id ASC")
  List<DirectMessage> findMessagesAfter(
      @Param("channelId") Long channelId,
      @Param("after") Long after,
      Pageable pageable
  );

  // before → 이전 메시지 조회 (DESC)
  @Query("SELECT dm FROM DirectMessage dm " +
      "JOIN FETCH dm.user s " +
      "JOIN FETCH dm.directMessageChannel c " +
      "WHERE c.id = :channelId AND dm.id < :before " +
      "ORDER BY dm.id DESC")
  List<DirectMessage> findMessagesBefore(
      @Param("channelId") Long channelId,
      @Param("before") Long before,
      Pageable pageable
  );

  @Query("SELECT dm FROM DirectMessage dm " +
      "JOIN FETCH dm.user s " +
      "JOIN FETCH dm.directMessageChannel c " +
      "WHERE c.id = :channelId " +
      "ORDER BY dm.id DESC")
  List<DirectMessage> findInitialMessages(
      @Param("channelId") Long channelId,
      Pageable pageable
  );
}
