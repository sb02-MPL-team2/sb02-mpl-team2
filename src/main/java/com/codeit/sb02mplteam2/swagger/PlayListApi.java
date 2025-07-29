package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "PlayList", description = "PlayList API")
public interface PlayListApi {

  @Operation(summary = "PlayList 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "PlayList 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = PlaylistDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User 또는 Content를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Content | User with id {ContentId | UserId} not found"))
      ),
  })
  ResponseEntity<PlaylistDto> create(
      @Parameter(
          description = "PlayList 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) PlaylistCreateRequest playListCreateRequest
  );

  @Operation(summary = "PlayList 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "PlayList가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "PlayList를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "PlayList with id {PlayListId} not found"))
      ),
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 PlayList ID") Long playListId
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "PlayList가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = PlaylistDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "PlayList를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "PlayList with id {PlayListId} not found"))
      ),
  })
  ResponseEntity<PlaylistDto> update(
      @Parameter(description = "수정할 PlayList ID") Long playListId,
      @Parameter(description = "수정할 PlayList 내용") PlaylistUpdateRequest request
  );

  @Operation(summary = "PlayList 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "PlayList 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = PlaylistDto.class))
      )
  })
  ResponseEntity<PlaylistDto> findById(
      @Parameter(description = "조회할 Channel ID") Long playListId
  );


  @Operation(summary = "PlayList 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "PlayList 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponsePlayListDto.class))
      )
  })
  ResponseEntity<CursorPageResponsePlayListDto> findAllByUserId(
      @Parameter(description = "조회할 Channel ID") Long userId,
      @Parameter(description = "페이징 커서 정보") LocalDateTime cursor,
      @Parameter(description = "페이징 정보", example = "{\"size\": 20, \"sort\": \"createdAt,desc\"}") Pageable pageable
  );

  @Operation(summary = "PlayList 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "PlayList 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = CursorPageResponsePlayListDto.class))
      )
  })
  ResponseEntity<CursorPageResponsePlayListDto> findAllByContentId(
      @Parameter(description = "조회할 Channel ID") Long contentId,
      @Parameter(description = "페이징 커서 정보") LocalDateTime cursor,
      @Parameter(description = "페이징 정보", example = "{\"size\": 20, \"sort\": \"createdAt,desc\"}") Pageable pageable
  );
}
