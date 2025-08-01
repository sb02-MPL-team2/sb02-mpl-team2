package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistUpdateRequest;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface PlaylistService {

  PlaylistDto create(PlaylistCreateRequest request);

  PlaylistDto subscribe(SubscribeRequest request);

  PlaylistDto unSubscribe(SubscribeRequest request);

  PlaylistDto update(Long id, PlaylistUpdateRequest request);

  PlaylistDto findById(Long id);

  void delete(Long id);

  CursorPageResponsePlayListDto findAllByContentId(Long contentId, LocalDateTime cursor, Pageable pageable);

  CursorPageResponsePlayListDto findAllByUserId(Long userId, LocalDateTime cursor, Pageable pageable);

}
