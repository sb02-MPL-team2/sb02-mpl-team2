package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.FollowRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name="Social", description="Social API")
public interface SocialApi {

  @Operation(summary = "사용자 팔로우")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "follow 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
    @PostMapping("/api/follows/{toUserId}")
    ResponseEntity<Void> follow(
        @Parameter(description = "팔로우할 유저 ID")
        @PathVariable Long toUserId,
        @RequestBody FollowRequest request
    );

  @Operation(summary="사용자 언팔로우")
  @ApiResponses(value={
      @ApiResponse(
          responseCode = "204", description = "unfollow 성공"
      )
  })
    @DeleteMapping("/api/follows/{toUserId}")
    ResponseEntity<Void> unfollow(
        @Parameter(description = "언팔로우할 유저 ID")
        @PathVariable Long toUserId
    );

//  @Operation(summary = "내가 팔로우한 유저 목록 조회")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공",
//          content = @Content(schema = @Schema(implementation = FollowingUserDto.class)))
//  })
//    @GetMapping("/api/follows/following")
//    ResponseEntity<List<FollowingUserDto>> getFollowing();
//
//  @Operation(summary = "나를 팔로우한 유저 목록 조회")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공",
//          content = @Content(schema = @Schema(implementation = FollowerUserDto.class)))
//  })
//  @GetMapping("/api/follows/followers")
//  ResponseEntity<List<FollowerUserDto>> getFollowers();
//
//  @Operation(summary = "팔로우한 유저들의 실시간 시청 정보 조회")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "실시간 시청 정보 조회 성공",
//          content = @Content(schema = @Schema(implementation = WatchingStatusDto.class)))
//  })
//  @GetMapping("/api/follows/watching")
//  ResponseEntity<List<WatchingStatusDto>> getWatchingStatus();
//
//  @Operation(summary = "팔로우 알림 조회")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "팔로우 알림 조회 성공",
//          content = @Content(schema = @Schema(implementation = FollowNotificationDto.class)))
//  })
//  @GetMapping("/api/notifications/follow")
//  ResponseEntity<List<FollowNotificationDto>> getFollowNotifications();

}
