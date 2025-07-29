package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

}
