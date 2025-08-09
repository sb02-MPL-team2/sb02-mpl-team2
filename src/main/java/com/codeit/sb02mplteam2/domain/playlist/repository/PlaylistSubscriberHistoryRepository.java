package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistSubscriberHistoryRepository extends JpaRepository<PlaylistSubscriberHistory,Long> {

  @Query("SELECT h FROm PlaylistSubscriberHistory h Where h.createdAt >= :startDate AND h.createdAt < :endDate")
  List<PlaylistSubscriberHistory> findDataFromLastSevenDays(
      @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

  List<PlaylistSubscriberHistory> findByPlaylistAndCreatedAtBetween(Playlist playlist,
      LocalDateTime startDate, LocalDateTime endDate);

}
