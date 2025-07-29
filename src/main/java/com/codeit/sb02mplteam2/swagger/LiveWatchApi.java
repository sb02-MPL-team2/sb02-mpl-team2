package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.livewatch.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "LiveWatch", description = "LiveWatch API")
public interface LiveWatchApi {

  @Operation(summary = "LiveChatMessage 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "LiveChatMessage가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = LiveChatMessageDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom 또는 User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom | User with id {chatRoomId | userId} not found"))
      ),
  })
  ResponseEntity<LiveChatMessageDto> createMessage(
      @Parameter(description = "채팅룸이 속한 Content Id") Long contentId,
      @Parameter(
          description = "LiveChatMessage 내용",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) LiveChatMessageCreateRequest liveChatMessageCreateRequest
  );

  @Operation(summary = "LiveChatRoom 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "LiveChatRoom 조회 성공",
          content = @Content(schema = @Schema(implementation = LiveChatRoomDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom for content id {contentId} not found"))
      )
  })
  ResponseEntity<LiveChatRoomDto> findChatRoomByContentId(
      @Parameter(description = "조회할 Content ID") Long contentId
  );

  @Operation(summary = "LiveChatRoom 참가자 입장")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "LiveChatRoom 입장 성공",
          content = @Content(schema = @Schema(implementation = LiveChatRoomJoinResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom for content id {contentId} not found"))
      )
  })
  ResponseEntity<LiveChatRoomJoinResponse> joinChatRoom(
      @Parameter(description = "입장할 Content ID") Long contentId
  );

  @Operation(summary = "LiveChatRoom 참가자 퇴장")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "LiveChatRoom 퇴장 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom for content id {contentId} not found"))
      )
  })
  ResponseEntity<Void> leaveChatRoom(
      @Parameter(description = "퇴장할 Content ID") Long contentId
  );

  @Operation(summary = "LiveChatRoom 활성 사용자 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "활성 사용자 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = LiveChatActiveUsersResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom for content id {contentId} not found"))
      )
  })
  ResponseEntity<LiveChatActiveUsersResponse> getActiveUsers(
      @Parameter(description = "조회할 Content ID") Long contentId
  );

  @Operation(summary = "LiveChatRoom 통계 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "LiveChatRoom 통계 조회 성공",
          content = @Content(schema = @Schema(implementation = LiveChatRoomStatsDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "LiveChatRoom을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "LiveChatRoom for content id {contentId} not found"))
      )
  })
  ResponseEntity<LiveChatRoomStatsDto> getChatRoomStats(
      @Parameter(description = "조회할 Content ID") Long contentId
  );
}
