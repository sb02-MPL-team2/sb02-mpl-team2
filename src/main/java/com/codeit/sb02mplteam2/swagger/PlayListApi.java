package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemListRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.swagger.content.ContentNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessCreationResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessDeleteResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessSingleRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistSuccessUpdateResponse;
import com.codeit.sb02mplteam2.swagger.playlist.PlaylistUnauthorizedResponse;
import com.codeit.sb02mplteam2.swagger.playlist.SubscribeNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.playlist.item.ItemSuccessInsertResponse;
import com.codeit.sb02mplteam2.swagger.playlist.item.PlaylistItemSuccessDeleteResponse;
import com.codeit.sb02mplteam2.swagger.user.UserNotFoundResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PlayList", description = "PlayList API")
public interface PlayListApi {

  @Operation(summary = "PlayList 생성")
  @PlaylistSuccessCreationResponse
  @UserNotFoundResponse
  @ContentNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> create(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "PlayList 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistCreateRequest request
  );

  @Operation(summary = "PlayList 구독하기")
  @PlaylistSuccessUpdateResponse
  @UserNotFoundResponse
  @PlaylistNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> subscribe(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "Subscribe 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody SubscribeRequest request
  );

  @Operation(summary = "PlayList 구독 취소하기")
  @PlaylistSuccessUpdateResponse
  @UserNotFoundResponse
  @PlaylistNotFoundResponse
  @SubscribeNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> unSubscribe(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "Subscribe 취소 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody SubscribeRequest request
  );

  @Operation(summary = "PlayList 내 Content 추가")
  @ItemSuccessInsertResponse
  @PlaylistNotFoundResponse
  @ContentNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> addContent(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "PlayList 내 Content 추가 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistItemRequest request
  );

  @Operation(summary = "PlayList 내 다수의 Content 추가")
  @ItemSuccessInsertResponse
  @PlaylistNotFoundResponse
  @ContentNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> addContentList(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "PlayList 내 Content 추가 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistItemListRequest request
  );

  @Operation(summary = "PlayList 삭제")
  @PlaylistSuccessDeleteResponse
  @PlaylistNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<Void> delete(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(description = "PlayList ID")
      Long playListId
  );

  @Operation(summary = "Playlist Content 삭제")
  @PlaylistItemSuccessDeleteResponse
  @PlaylistNotFoundResponse
  @ContentNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<Void> deleteItemByContentId(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "PlayList 내 Content 추가 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      @RequestBody PlaylistItemRequest request);

  @Operation(summary = "Playlist Content 전체 삭제")
  @PlaylistItemSuccessDeleteResponse
  @PlaylistNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<Void> deleteAllItemByPlaylistId(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(description = "PlayList ID")
      Long playListId);


  @Operation(summary = "Message 내용 수정")
  @PlaylistSuccessUpdateResponse
  @PlaylistNotFoundResponse
  @PlaylistUnauthorizedResponse
  ResponseEntity<PlaylistDto> update(
      @AuthenticationPrincipal MplUserDetails userDetails,
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
}
