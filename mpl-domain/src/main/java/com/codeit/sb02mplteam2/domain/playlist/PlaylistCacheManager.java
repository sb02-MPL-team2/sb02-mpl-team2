package com.codeit.sb02mplteam2.domain.playlist;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaylistCacheManager {

  private final CacheManager cacheManager;

  public void evictAndPut(PlaylistDto playlistDto) {
    cacheManager.getCache("playlists").put(playlistDto.id(), playlistDto);
  }
}
