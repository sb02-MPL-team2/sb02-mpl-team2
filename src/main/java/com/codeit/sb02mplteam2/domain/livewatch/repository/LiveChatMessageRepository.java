package com.codeit.sb02mplteam2.domain.livewatch.repository;

import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveChatMessageRepository extends JpaRepository<LiveChatMessage, Long> {

}
