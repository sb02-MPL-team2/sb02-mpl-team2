package com.codeit.sb02mplteam2.domain.social.controller;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import com.codeit.sb02mplteam2.domain.social.service.DirectMessageChannelService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class DirectMessageChannelController {

  private final DirectMessageChannelService directMessageChannelService;

  @PostMapping("/{senderId}")
  public ResponseEntity<DirectMessageChannelResponse> create(
      @PathVariable Long senderId,
      @RequestParam Long receiverId
  ) {
    DirectMessageChannelResponse response = directMessageChannelService.create(senderId,
        receiverId);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{channelId}/channel")
  public ResponseEntity<DirectMessageChannelResponse> findByChannelId(
      @PathVariable Long channelId,
      @RequestParam Long userId) {
    DirectMessageChannelResponse response = directMessageChannelService.findByChannelId(channelId, userId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<CursorPageResponseDirectMessageChannelDto> findAll(
      @PathVariable Long userId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
      @RequestParam(defaultValue = "20") int size
  ){
    CursorPageResponseDirectMessageChannelDto response = directMessageChannelService.findAll(userId, cursor, size);
    return ResponseEntity.ok(response);
  }
}
