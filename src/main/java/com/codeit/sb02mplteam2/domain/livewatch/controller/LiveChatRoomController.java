package com.codeit.sb02mplteam2.domain.livewatch.controller;

import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import com.codeit.sb02mplteam2.domain.livewatch.service.LiveWatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livewatch/rooms")
@RequiredArgsConstructor
public class LiveChatRoomController {

    private final LiveWatchService liveWatchService;


    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomJoinResponse> joinRoom(@PathVariable Long roomId) {
        Long userId = getCurrentUserId();
        RoomJoinResponse response = liveWatchService.joinRoomWithInfo(roomId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId) {
        Long userId = getCurrentUserId();
        liveWatchService.leaveRoom(roomId, userId);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<ChatMessagePageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "30") Integer size) {
        ChatMessagePageResponse response = liveWatchService.getMessages(roomId, cursor, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/participant-count")
    public ResponseEntity<Integer> getParticipantCount(@PathVariable Long roomId) {
        Integer count = liveWatchService.getParticipantCount(roomId);
        return ResponseEntity.ok(count);
    }



    // TODO: JWT에서 사용자 ID 가져오기
    private Long getCurrentUserId() {
        // 임시 구현 - 실제로는 Security Context에서 가져와야 함
        return 1L;
    }
}