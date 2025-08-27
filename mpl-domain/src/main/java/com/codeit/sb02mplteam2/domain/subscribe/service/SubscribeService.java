package com.codeit.sb02mplteam2.domain.subscribe.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;

public interface SubscribeService {
  PlaylistDto subscribe(Long userId, SubscribeRequest request);

  PlaylistDto unSubscribe(Long userId, SubscribeRequest request);
}
