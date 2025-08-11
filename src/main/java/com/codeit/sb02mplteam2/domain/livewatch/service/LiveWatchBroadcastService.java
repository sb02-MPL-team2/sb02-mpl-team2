package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ChatMessageDto;
import com.codeit.sb02mplteam2.domain.livewatch.dto.websocket.ErrorNotificationDto;

public interface LiveWatchBroadcastService {

  void broadcastMessage(Long roomId, ChatMessageDto message);

  void broadcastParticipantEvent(Long roomId, ChatMessageDto eventMessage);

  void sendErrorToUser(String username, ErrorNotificationDto error);

  void broadcastSystemMessage(Long roomId, String message);
}