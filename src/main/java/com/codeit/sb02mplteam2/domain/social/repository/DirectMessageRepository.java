package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {


  @Query("""
        SELECT m
        FROM DirectMessage m
        WHERE m.directMessageChannel.id = :channelId
          AND (:cursor IS NULL OR m.createdAt < :cursor)
        ORDER BY m.createdAt DESC
    """)
  List<DirectMessage> findMessagesByChannelWithCursor(Long channelId, LocalDateTime cursor, Pageable pageable);

}
