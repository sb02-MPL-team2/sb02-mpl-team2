package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import java.util.List;

public interface PlaylistItemService {
  PlaylistDto addContent(Long playlistId, Long contentId);

  PlaylistDto addContentList(Long playlistId, List<Long> contentIds);

  void deleteAllByPlaylistId(Long playlistId);

  void deleteByContentId(Long playlistId, Long contentId);
}
