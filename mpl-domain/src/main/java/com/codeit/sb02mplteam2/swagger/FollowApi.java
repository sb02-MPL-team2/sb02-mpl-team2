package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseFollowDto;
import com.codeit.sb02mplteam2.domain.social.dto.FollowResponse;
import com.codeit.sb02mplteam2.domain.social.dto.FollowStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "Follow", description = "Follow API")
public interface FollowApi {

  @Operation(summary = "사용자 팔로우")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "follow 성공",
          content = @Content(schema = @Schema(implementation = FollowResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @PostMapping("/api/follows/{followeeId}")
  ResponseEntity<FollowResponse> follow(
      @Parameter(description = "팔로우할 유저 ID")
      @PathVariable Long followeeId,
      @Parameter(description = "팔로우하는 유저 ID")
      @RequestParam Long followerId
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
  @DeleteMapping("/api/follows/{followeeId}")
  ResponseEntity<FollowResponse> unfollow(
      @Parameter(description = "언팔로우할 유저 ID")
      @PathVariable Long followeeId,
      @Parameter(description = "언팔로우하는 유저 ID")
      @RequestParam Long followerId
  );

  @Operation(summary = "팔로우 확인")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로우 확인",
          content = @Content(schema = @Schema(implementation = FollowStatusResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/{followeeId}")
  ResponseEntity<FollowStatusResponse> followCheck(
      @Parameter(description = "팔로우하고 있는지 확인할 유저 ID")
      @PathVariable Long followeeId,
      @Parameter(description = "사용자 ID")
      @RequestParam Long followerId
  );

  @Operation(summary = "팔로잉 유저 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseFollowDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/{userId}/followings")
  ResponseEntity<CursorPageResponseFollowDto> getFollowing(
      @Parameter(description = "팔로잉 목록 조회할 유저 ID")
      @PathVariable Long userId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
      @RequestParam(defaultValue = "20") int size
  );

  @Operation(summary = "팔로워 유저 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseFollowDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/follows/{userId}/followers")
  ResponseEntity<CursorPageResponseFollowDto> getFollowers(
      @Parameter(description = "팔로워 목록 조회할 유저 ID")
      @PathVariable Long userId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
      @RequestParam(defaultValue = "20") int size
  );

}
