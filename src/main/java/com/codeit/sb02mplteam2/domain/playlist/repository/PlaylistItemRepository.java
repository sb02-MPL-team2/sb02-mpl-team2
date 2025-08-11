package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {

  @Query("select pi from PlaylistItem pi left join fetch pi.content where pi.id = :id")
  Optional<PlaylistItem> findById(Long id);

}
