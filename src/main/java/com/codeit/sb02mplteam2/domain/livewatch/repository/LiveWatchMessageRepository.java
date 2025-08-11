package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchMessage;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LiveWatchMessageRepository extends JpaRepository<LiveWatchMessage, Long> {

  List<LiveWatchMessage> findByLiveWatchRoomId(Long roomId, Pageable pageable);

  @Query("""
        SELECT m FROM LiveWatchMessage m
        WHERE m.liveWatchRoom.id = :roomId
        AND (m.sentAt < :cursorSentAt OR (m.sentAt = :cursorSentAt AND m.id < :cursorId))
        ORDER BY m.sentAt DESC, m.id DESC
      """)
  List<LiveWatchMessage> findMessagesWithCursor(
      @Param("roomId") Long roomId,
      @Param("cursorSentAt") Instant cursorSentAt,
      @Param("cursorId") Long cursorId,
      Pageable pageable
  );
}