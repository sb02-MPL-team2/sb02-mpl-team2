package com.codeit.sb02mplteam2.domain.social.controller;

import com.codeit.sb02mplteam2.domain.social.dto.FollowRequest;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import com.codeit.sb02mplteam2.domain.social.service.FollowService;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

  private final FollowService followService;

  @PostMapping("/{followeeId}")
  public ResponseEntity<FollowResponse> follow(
      @PathVariable Long followeeId,
      @RequestParam Long followerId
  ) {
    FollowResponse response = followService.create(followeeId, followerId);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{followeeId}")
  public ResponseEntity<FollowResponse> unfollow(
      @PathVariable Long followeeId,
      @RequestParam Long followerId
  ) {
    FollowResponse response = followService.delete(followeeId, followerId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{followeeId}")
  public ResponseEntity<FollowStatusResponse> followCheck(
      @PathVariable Long followeeId,
      @RequestParam Long followerId
  ){
    FollowStatusResponse response = followService.isFollowing(followeeId, followerId);

    return ResponseEntity.ok(response);
  }

//  @GetMapping("/following/{userId}")
//  public ResponseEntity<List<UserDto>> getFollowings(@PathVariable Long userId){
//
//  }
//
//  @GetMapping("/follower/{userId}")
//  public ResponseEntity<List<UserDto>> getFollowers(
//
//  ){
//
//  }


}
