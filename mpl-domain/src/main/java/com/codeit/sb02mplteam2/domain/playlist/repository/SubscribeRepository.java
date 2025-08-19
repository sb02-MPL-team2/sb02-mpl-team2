package com.codeit.sb02mplteam2.domain.playlist.repository;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

  @Query("SELECT s FROM Subscribe s "
      + "LEFT JOIN FETCH s.user u "
      + "LEFT JOIN FETCH s.playlist p "
      + "WHERE u = :user AND p = :playlist")
  Optional<Subscribe> findByUserAndPlaylist(User user, Playlist playlist);
}
