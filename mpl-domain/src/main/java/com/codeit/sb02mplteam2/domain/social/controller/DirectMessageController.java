package com.codeit.sb02mplteam2.domain.social.controller;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
import com.codeit.sb02mplteam2.domain.social.service.DirectMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dms")
public class DirectMessageController {

  private final DirectMessageService directMessageService;

  @PostMapping("/{userId}")
  public ResponseEntity<DirectMessageResponse> create(
      @RequestPart("directMessageCreateRequest") DirectMessageCreateRequest request
  ){
    DirectMessageResponse response = directMessageService.create(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{channelId}/messages")
  public ResponseEntity<CursorPageResponseDirectMessageDto> findAll(
      @PathVariable Long channelId,
      @RequestParam(required = false) Long after,
      @RequestParam(required = false) Long before,
      @RequestParam(defaultValue = "20") int limit
  ){
    CursorPageResponseDirectMessageDto response = directMessageService.findAll(channelId, after, before, limit);
    return ResponseEntity.ok(response);
  }


}
