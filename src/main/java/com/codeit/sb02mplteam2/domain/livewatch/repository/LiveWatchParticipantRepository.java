package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LiveWatchParticipantRepository extends JpaRepository<LiveWatchParticipant, Long> {

  boolean existsByLiveWatchRoomIdAndUserId(Long roomId, Long userId);

  @Query("SELECT p FROM LiveWatchParticipant p JOIN FETCH p.user u WHERE p.liveWatchRoom.id = :roomId")
  List<LiveWatchParticipant> findByLiveWatchRoomIdWithUserFetchJoin(@Param("roomId") Long roomId);

  List<LiveWatchParticipant> findByUserId(Long userId);

  int countByLiveWatchRoomId(Long roomId);

  void deleteByLiveWatchRoomIdAndUserId(Long roomId, Long userId);

  Optional<LiveWatchParticipant> findFirstByUserId(Long userId);

  @Query("""
      SELECT p FROM LiveWatchParticipant p
      JOIN FETCH p.user u
      JOIN FETCH p.liveWatchRoom r
      WHERE r.id = :roomId AND u.id = :userId
      """)
  Optional<LiveWatchParticipant> findByLiveWatchRoomIdAndUserIdWithFetchJoins(
      @Param("roomId") Long roomId,
      @Param("userId") Long userId
  );
}