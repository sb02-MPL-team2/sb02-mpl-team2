package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;

public interface PlaylistService {

  PlaylistDto create(Long userId, PlaylistCreateRequest request);

  PlaylistDto subscribe(Long userId, SubscribeRequest request);

  PlaylistDto unSubscribe(Long userId, SubscribeRequest request);

  PlaylistDto update(Long userId, Long playlistId, PlaylistUpdateRequest request);

  PlaylistDto findById(Long id);

  PlaylistDto refreshAndFindById(Long id);

  void delete(Long playlistId, Long userId);

}
