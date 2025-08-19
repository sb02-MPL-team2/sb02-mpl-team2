package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageChannelDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageChannelResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "DirectMessageChannel", description = "DirectMessageChannel API")
public interface DirectMessageChannelApi {

  @Operation(summary = "DM Channel 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "DM Channel 생성 성공",
          content = @Content(schema = @Schema(implementation = DirectMessageChannelResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @PostMapping("/api/channels/{senderId}")
  ResponseEntity<DirectMessageChannelResponse> create(
      @Parameter(description = "보내는 유저 ID")
      @PathVariable Long senderId,
      @Parameter(description = "받는 유저 ID")
      @RequestParam Long receiverId
  );

  @Operation(summary = "DM Channel 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DM Channel 조회 성공",
          content = @Content(schema = @Schema(implementation = DirectMessageChannelResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "채널을 찾을 수 없음"
      )
  })
  @GetMapping("/api/channels/{channelId}/channel")
  ResponseEntity<DirectMessageChannelResponse> findByChannelId(
      @PathVariable Long channelId,
      @Parameter(description = "사용자 ID")
      @RequestParam Long userId
  );

  @Operation(summary = "DM Channel 전체 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DM Channel 전체 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseDirectMessageChannelDto.class))
      )
  })
  @GetMapping("/api/channels/{userId}")
  ResponseEntity<CursorPageResponseDirectMessageChannelDto> findAll(
      @Parameter(description = "사용자 ID")
      @PathVariable Long userId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
      @RequestParam(defaultValue = "20") int size
  );

}
