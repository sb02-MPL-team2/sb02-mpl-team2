package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface PlaylistSearchService {
  CursorPageResponsePlayListDto findAllByUserId(Long userId, LocalDateTime cursor, Pageable pageable);


}
