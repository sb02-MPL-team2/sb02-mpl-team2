package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, Long> {


}
