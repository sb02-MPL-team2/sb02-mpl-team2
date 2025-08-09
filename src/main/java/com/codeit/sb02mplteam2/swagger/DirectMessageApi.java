package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.CursorPageResponseDirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageCreateRequest;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "DirectMessage", description = "DirectMessage API")
public interface DirectMessageApi {

  @Operation(summary = "DM 생성")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "DM 생성 성공",
          content = @Content(schema = @Schema(implementation = DirectMessageResponse.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @PostMapping("/api/dms/{userId}")
  ResponseEntity<DirectMessageResponse> create(
      @Parameter(description = "DM 생성 정보")
      @RequestBody DirectMessageCreateRequest request
  );

  @Operation(summary = "DM 전체 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "DM 전체 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponseDirectMessageDto.class))
      ),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"
      )
  })
  @GetMapping("/api/dms/{channelId}/messages")
  ResponseEntity<CursorPageResponseDirectMessageDto> findAll(
      @PathVariable Long channelId,
      @RequestParam Long fromId,
      @RequestParam Long toId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
      @RequestParam(defaultValue = "20") int size

  );
}
