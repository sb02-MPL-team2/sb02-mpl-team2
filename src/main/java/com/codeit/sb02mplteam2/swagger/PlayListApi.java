package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemListRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.swagger.content.ContentNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.playlist.ItemSuccessInsertResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessCreationResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessDeleteResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessSingleRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.user.UserNotFoundResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PlayList", description = "PlayList API")
public interface PlayListApi {

  @Operation(summary = "PlayList 생성")
  @PlaylistSuccessCreationResponse
  @UserNotFoundResponse
  @ContentNotFoundResponse
  ResponseEntity<PlaylistDto> create(
      @Parameter(
          description = "PlayList 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistCreateRequest request
  );

  @Operation(summary = "PlayList 내 Content 추가")
  @ItemSuccessInsertResponse
  @PlaylistNotFoundResponse
  @ContentNotFoundResponse
  ResponseEntity<PlaylistDto> addContent(
      @Parameter(
          description = "PlayList 내 Content 추가 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistItemRequest request
  );

  @Operation(summary = "PlayList 내 Content 추가")
  @ItemSuccessInsertResponse
  @PlaylistNotFoundResponse
  @ContentNotFoundResponse
  ResponseEntity<PlaylistDto> addContentList(
      @Parameter(
          description = "PlayList 내 Content 추가 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistItemListRequest request
  );

  @Operation(summary = "PlayList 삭제")
  @PlaylistSuccessDeleteResponse
  @PlaylistNotFoundResponse
  ResponseEntity<Void> delete(
      @Parameter(description = "PlayList ID")
      Long playListId
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponse(
      responseCode = "200", description = "PlayList가 성공적으로 수정됨",
      content = @Content(schema = @Schema(implementation = PlaylistDto.class))
  )
  @PlaylistNotFoundResponse
  ResponseEntity<PlaylistDto> update(
      @Parameter(description = "수정할 PlayList ID")
      Long playListId,
      @Parameter(description = "수정할 PlayList 내용")
      PlaylistUpdateRequest request
  );

  @Operation(summary = "PlayList 단건 조회")
  @PlaylistSuccessSingleRetrievalResponse
  @PlaylistNotFoundResponse
  ResponseEntity<PlaylistDto> findById(
      @Parameter(description = "조회할 playlist ID")
      Long playListId
  );

  @Operation(summary = "PlayList 목록 조회")
  @PlaylistSuccessRetrievalResponse
  @PlaylistNotFoundResponse
  ResponseEntity<CursorPageResponsePlayListDto> findAllByUserId(
      @Parameter(description = "조회할 user ID")
      Long userId,
      @Parameter(description = "페이징 커서 정보")
      LocalDateTime cursor,
      @Parameter(description = "페이징 정보", example = "{\"size\": 20, \"sort\": \"createdAt,desc\"}")
      Pageable pageable
  );

  @Operation(summary = "PlayList 목록 조회")
  @PlaylistSuccessRetrievalResponse
  @ContentNotFoundResponse
  @PlaylistNotFoundResponse
  ResponseEntity<CursorPageResponsePlayListDto> findAllByContentId(
      @Parameter(description = "조회할 content ID")
      Long contentId,
      @Parameter(description = "페이징 커서 정보")
      LocalDateTime cursor,
      @Parameter(description = "페이징 정보", example = "{\"size\": 20, \"sort\": \"createdAt,desc\"}")
      Pageable pageable
  );
}
