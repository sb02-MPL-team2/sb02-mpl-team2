package com.codeit.sb02mplteam2.domain.playlist.controller;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemListRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistItemRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistItemService;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistSearchService;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistService;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.swagger.PlayListApi;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController implements PlayListApi {

  private final PlaylistService playlistService;
  private final PlaylistItemService playlistItemService;
  private final PlaylistSearchService playlistSearchService;

  @Override
  @PostMapping
  public ResponseEntity<PlaylistDto> create(@RequestBody PlaylistCreateRequest request) {
    PlaylistDto playlistDto = playlistService.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @PostMapping("/subscribe")
  public ResponseEntity<PlaylistDto> subscribe(SubscribeRequest request) {
    PlaylistDto playlistDto = playlistService.subscribe(request);
    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @DeleteMapping("/unsubscribe")
  public ResponseEntity<PlaylistDto> unSubscribe(SubscribeRequest request) {
    PlaylistDto playlistDto = playlistService.unSubscribe(request);

    return ResponseEntity.ok(playlistDto);
  }

  @Override
  @PostMapping("/add")
  public ResponseEntity<PlaylistDto> addContent(@RequestBody PlaylistItemRequest request) {
    PlaylistDto playlistDto = playlistItemService.addContent(request.playListId(), request.contentId());

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @PostMapping("/add-list")
  public ResponseEntity<PlaylistDto> addContentList(PlaylistItemListRequest request) {
    PlaylistDto playlistDto = playlistItemService.addContentList(request.playListId(),
        request.contentIds());

    return ResponseEntity.status(HttpStatus.CREATED).body(playlistDto);
  }

  @Override
  @DeleteMapping("/{playListId}")
  public ResponseEntity<Void> delete(@PathVariable Long playListId) {
    playlistService.delete(playListId);

    return ResponseEntity.noContent().build();
  }

  @Override
  @PatchMapping("/{playListId}")
  public ResponseEntity<PlaylistDto> update(@PathVariable Long playListId, @RequestBody PlaylistUpdateRequest request) {
    PlaylistDto playlistDto = playlistService.update(playListId, request);

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
          sort = "createdAt,desc"
      )
      Pageable pageable) {
    CursorPageResponsePlayListDto response = playlistSearchService.findAllByUserId(userId, cursor, pageable);
    return ResponseEntity.ok(response);
  }
}
