package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.FollowRequest;
import com.codeit.sb02mplteam2.domain.social.dto.FollowerUserDto;
import com.codeit.sb02mplteam2.domain.social.dto.FollowingUserDto;
import com.codeit.sb02mplteam2.domain.social.dto.WatchingStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "Follow", description = "Follow API")
public interface FollowApi {

  @Operation(summary = "사용자 팔로우")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "follow 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @PostMapping("/api/follows/{userId}")
  ResponseEntity<Void> follow(
      @Parameter(description = "팔로우할 유저 ID")
      @PathVariable Long userId,
      @RequestBody FollowRequest request
  );

  @Operation(summary = "사용자 언팔로우")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "unfollow 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @DeleteMapping("/api/follows/{userId}")
  ResponseEntity<Void> unfollow(
      @Parameter(description = "언팔로우할 유저 ID")
      @PathVariable Long userId,
      @RequestBody FollowRequest request
  );

  @Operation(summary = "팔로잉 유저 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = FollowingUserDto.class)))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/following/{userId}")
  ResponseEntity<List<FollowingUserDto>> getFollowing(
      @Parameter(description = "팔로잉 목록 조회할 유저 ID")
      @PathVariable Long userId
  );

  @Operation(summary = "팔로워 유저 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = FollowerUserDto.class)))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/followers/{userId}")
  ResponseEntity<List<FollowerUserDto>> getFollowers(
      @Parameter(description = "팔로워 목록 조회할 유저 ID")
      @PathVariable Long userId
  );

  @Operation(summary = "유저 실시간 시청 정보 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "실시간 시청 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = WatchingStatusDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/watching/{userId}")
  ResponseEntity<WatchingStatusDto> getWatchingStatus();


}
