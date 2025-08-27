package com.codeit.sb02mplteam2.domain.playlist.controller;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemListRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistItemService;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistSearchService;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistService;
import com.codeit.sb02mplteam2.domain.subscribe.service.SubscribeService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.swagger.PlayListApi;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController implements PlayListApi {

  private final PlaylistService playlistService;
  private final SubscribeService subscribeService;
  private final PlaylistItemService playlistItemService;
  private final PlaylistSearchService playlistSearchService;

  @Override
  @PostMapping
  public ResponseEntity<PlaylistDto> create(@AuthenticationPrincipal MplUserDetails userDetails,
      @RequestBody PlaylistCreateRequest request) {
    PlaylistDto playlistDto = playlistService.create(userDetails.getUserDto().id(), request);

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @PostMapping("/subscribe")
  public ResponseEntity<PlaylistDto> subscribe(@AuthenticationPrincipal MplUserDetails userDetails,
      SubscribeRequest request) {
    PlaylistDto playlistDto = subscribeService.subscribe(userDetails.getUserDto().id(), request);
    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @DeleteMapping("/unsubscribe")
  public ResponseEntity<PlaylistDto> unSubscribe(
      @AuthenticationPrincipal MplUserDetails userDetails, SubscribeRequest request) {
    PlaylistDto playlistDto = subscribeService.unSubscribe(userDetails.getUserDto().id(), request);

    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @PostMapping("/items")
  public ResponseEntity<PlaylistDto> addContent(
      @AuthenticationPrincipal MplUserDetails userDetails, @RequestBody PlaylistItemRequest request) {
    PlaylistDto playlistDto = playlistItemService.addContent(userDetails.getUserDto().id(), request.playListId(),
        request.contentId());

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @PostMapping("/items/bulk")
  public ResponseEntity<PlaylistDto> addContentList(
      @AuthenticationPrincipal MplUserDetails userDetails, @RequestBody PlaylistItemListRequest request) {
    PlaylistDto playlistDto = playlistItemService.addContentList(userDetails.getUserDto().id(), request.playListId(),
        request.contentIds());

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @DeleteMapping("/{playListId}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal MplUserDetails userDetails, @PathVariable Long playListId) {
    playlistService.delete(playListId, userDetails.getUserDto().id());

    return ResponseEntity.noContent().build();
  }

  @Override
  @DeleteMapping("/items")
  public ResponseEntity<Void> deleteItemByContentId(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @RequestBody PlaylistItemRequest request) {
    playlistItemService.deleteByContentId(userDetails.getUserDto().id(), request.playListId(),
        request.contentId());
    return ResponseEntity.noContent().build();
  }

  @Override
  @DeleteMapping("/{playListId}/items")
  public ResponseEntity<Void> deleteAllItemByPlaylistId(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @PathVariable Long playListId) {
    playlistItemService.deleteAllByPlaylistId(userDetails.getUserDto().id(), playListId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PatchMapping("/{playListId}")
  public ResponseEntity<PlaylistDto> update(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @PathVariable Long playListId,
      @RequestBody PlaylistUpdateRequest request) {
    PlaylistDto playlistDto = playlistService.update(userDetails.getUserDto().id(), playListId, request);

    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @GetMapping("/{playListId}")
  public ResponseEntity<PlaylistDto> findById(@PathVariable Long playListId) {
    PlaylistDto playlistDto = playlistService.findById(playListId);
    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @GetMapping("/user/{userId}")
  public ResponseEntity<CursorPageResponsePlayListDto> findAllByUserId(
      @PathVariable Long userId,
      @RequestParam(value = "cursor", required = false) LocalDateTime cursor,
      @PageableDefault(
          size = 20,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      )
      Pageable pageable) {
    CursorPageResponsePlayListDto response = playlistSearchService.findAllByUserId(userId, cursor,
        pageable);
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping
  public ResponseEntity<CursorPageResponsePlayListDto> findAll(
      @RequestParam(value = "cursor", required = false) LocalDateTime cursor,
      @PageableDefault(
          size = 20,
          page = 0,
          sort = "createdAt",
          direction = Direction.DESC
      )
      Pageable pageable) {
    CursorPageResponsePlayListDto response = playlistSearchService.findAll(cursor, pageable);
    return ResponseEntity.ok(response);
  }
}
