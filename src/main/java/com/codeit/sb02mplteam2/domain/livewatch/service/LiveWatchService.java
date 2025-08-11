package com.codeit.sb02mplteam2.domain.livewatch.service;

import com.codeit.sb02mplteam2.domain.livewatch.dto.request.SendMessageRequest;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import com.codeit.sb02mplteam2.domain.livewatch.entity.LiveWatchRoom;

import java.util.List;

public interface LiveWatchService {

  LiveWatchRoom createRoom(Long contentId, Long userId, String title);

  void sendMessage(SendMessageRequest request, Long userId);

  ChatMessagePageResponse getMessages(Long roomId, String cursor, Integer size);

  RoomJoinResponse joinAndGetRoomInfo(Long roomId, Long userId);

  void leaveRoom(Long roomId, Long userId);

  Integer getParticipantCount(Long roomId);

  void removeParticipantFromRoom(Long roomId, Long userId);

  void handleUserDisconnect(Long userId);
}