package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveWatchRoomRepository extends JpaRepository<LiveWatchRoom, Long> {

  Optional<LiveWatchRoom> findByContentId(Long contentId);

}