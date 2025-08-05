package com.codeit.sb02mplteam2.domain.social.controller;

import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.social.service.DirectMessageChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
  ){
      DirectMessageChannelResponse response = directMessageChannelService.create(senderId, receiverId);
      return ResponseEntity.ok(response);
  }
}
