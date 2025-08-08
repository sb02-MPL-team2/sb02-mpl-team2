package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveChatRoomRepository extends JpaRepository<LiveChatRoom, Long> {
    
}