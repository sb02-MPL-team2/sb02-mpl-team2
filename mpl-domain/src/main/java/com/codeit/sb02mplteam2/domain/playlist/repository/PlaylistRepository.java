package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

  @NonNull
  @Query("""
      SELECT p from Playlist p
            left join fetch p.user
            left join fetch p.subscribes
            left join fetch p.items i
                  left join fetch i.content
            where p.id = :id""")
  Optional<Playlist> findById(@NonNull Long id);

  @Query("""
      SELECT p FROM Playlist p
            LEFT JOIN FETCH p.user u
            left join fetch p.subscribes
            left join fetch p.items i
                left join fetch i.content
            WHERE u.id = :userId AND (:#{#cursor} IS NULL OR p.createdAt < :cursor)
      """)
  Slice<Playlist> findAllByUserId(@Param("userId") Long userId,
      @Param("cursor") LocalDateTime cursor, Pageable pageable);

  @Query("""
      SELECT p FROM Playlist p
            LEFT JOIN FETCH p.user u
            left join fetch p.subscribes
            left join fetch p.items i
                left join fetch i.content
            WHERE (:#{#cursor} IS NULL OR p.createdAt < :cursor)""")
  Slice<Playlist> findAllByCursor(LocalDateTime cursor, Pageable attr0);
}
