package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveChatParticipantRepository extends JpaRepository<LiveChatParticipant, Long> {
    
}