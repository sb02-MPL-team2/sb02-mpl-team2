package com.codeit.sb02mplteam2.domain.subscribe.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import java.util.List;

public interface SubscribeService {
  PlaylistDto subscribe(Long userId, SubscribeRequest request);

  PlaylistDto unSubscribe(Long userId, SubscribeRequest request);

  List<PlaylistDto> findAllBySubscribed();

  List<PlaylistDto> findAllBySubscribed(Long userId);

  List<PlaylistDto> findAllByUnSubscribed(Long userId);
}
