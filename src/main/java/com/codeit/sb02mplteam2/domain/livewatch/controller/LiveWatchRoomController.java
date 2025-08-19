package com.codeit.sb02mplteam2.domain.livewatch.controller;

import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livewatch/rooms")
@RequiredArgsConstructor
public class LiveWatchRoomController {

  private final LiveWatchService liveWatchService;

  @GetMapping("/content/{contentId}")
  public ResponseEntity<RoomJoinResponse> getOrCreateRoomByContent(@PathVariable Long contentId,
      @AuthenticationPrincipal MplUserDetails userDetails) {
    Long userId = userDetails.getId();
    
    // 콘텐츠별 방을 찾거나 생성하고 자동으로 참여
    RoomJoinResponse response = liveWatchService.getOrCreateRoomByContentAndJoin(contentId, userId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{roomId}/join")
  public ResponseEntity<RoomJoinResponse> joinRoom(@PathVariable Long roomId,
      @AuthenticationPrincipal MplUserDetails userDetails) {
    Long userId = userDetails.getId();
    RoomJoinResponse response = liveWatchService.joinAndGetRoomInfo(roomId, userId);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{roomId}/leave")
  public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId,
      @AuthenticationPrincipal MplUserDetails userDetails) {
    Long userId = userDetails.getId();
    liveWatchService.leaveRoom(roomId, userId);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/{roomId}/messages")
  public ResponseEntity<ChatMessagePageResponse> getMessages(@PathVariable Long roomId,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "30") Integer size) {
    ChatMessagePageResponse response = liveWatchService.getMessages(roomId, cursor, size);
    return ResponseEntity.ok(response);
  }

  //사용 안 될 가능성 있음
  @GetMapping("/{roomId}/participant-count")
  public ResponseEntity<Integer> getParticipantCount(@PathVariable Long roomId) {
    Integer count = liveWatchService.getParticipantCount(roomId);
    return ResponseEntity.ok(count);
  }
}