package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;


public interface LiveWatchService {

    void sendMessage(SendMessageRequest request, Long userId);

    ChatMessagePageResponse getMessages(Long roomId, String cursor, Integer size);
    
    RoomJoinResponse joinRoomWithInfo(Long roomId, Long userId);
    
    void leaveRoom(Long roomId, Long userId);
    
    Integer getParticipantCount(Long roomId);
}