package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.livewatch.dto.response.ChatMessagePageResponse;
import com.codeit.sb02mplteam2.domain.livewatch.dto.response.RoomJoinResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "LiveWatch", description = "실시간 채팅방 API")
public interface LiveWatchApi {

  @Operation(summary = "채팅방 입장", description = "채팅방에 입장하고 방 정보와 참여자 목록을 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "채팅방 입장 성공",
          content = @Content(schema = @Schema(implementation = RoomJoinResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "채팅방을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "ChatRoom with id {roomId} not found"))
      ),
      @ApiResponse(
          responseCode = "409", description = "이미 다른 채팅방에 참여 중",
          content = @Content(examples = @ExampleObject(value = "User is already participating in another room"))
      )
  })
  ResponseEntity<RoomJoinResponse> joinRoom(
      @Parameter(description = "입장할 채팅방 ID") Long roomId
  );

  @Operation(summary = "채팅방 퇴장", description = "현재 참여 중인 채팅방에서 퇴장합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "채팅방 퇴장 성공"),
      @ApiResponse(
          responseCode = "404", description = "채팅방을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "ChatRoom with id {roomId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "채팅방에 참여하지 않은 사용자",
          content = @Content(examples = @ExampleObject(value = "User is not participating in this room"))
      )
  })
  ResponseEntity<Void> leaveRoom(
      @Parameter(description = "퇴장할 채팅방 ID") Long roomId
  );

  @Operation(summary = "메시지 히스토리 조회", description = "채팅방의 메시지 히스토리를 페이지네이션으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "메시지 히스토리 조회 성공",
          content = @Content(schema = @Schema(implementation = ChatMessagePageResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "채팅방을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "ChatRoom with id {roomId} not found"))
      )
  })
  ResponseEntity<ChatMessagePageResponse> getMessages(
      @Parameter(description = "조회할 채팅방 ID") Long roomId,
      @Parameter(description = "페이지네이션 커서 (이전 조회의 마지막 메시지 시간)", required = false) String cursor,
      @Parameter(description = "조회할 메시지 수 (기본: 30)", required = false) Integer size
  );

  @Operation(summary = "현재 참여자 수 조회", description = "채팅방의 현재 참여자 수를 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "참여자 수 조회 성공",
          content = @Content(schema = @Schema(implementation = Integer.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "채팅방을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "ChatRoom with id {roomId} not found"))
      )
  })
  ResponseEntity<Integer> getParticipantCount(
      @Parameter(description = "조회할 채팅방 ID") Long roomId
  );
}