package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

  @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.user u WHERE u.id = :userId AND p.createdAt < :cursor")
  Slice<Playlist> findAllByUserId(@Param("userId") Long userId,
      @Param("cursor") LocalDateTime cursor, Pageable pageable);
}
