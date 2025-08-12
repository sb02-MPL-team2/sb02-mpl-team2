package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.playlist.PlaylistUtil;
import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistSlimDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicPlaylistSearchService implements PlaylistSearchService{

  private final PlaylistRepository playlistRepository;

  @Override
  public CursorPageResponsePlayListDto findAll(LocalDateTime cursor, Pageable pageable) {
    Slice<Playlist> slice = playlistRepository.findAllByCursor(cursor, pageable);

    return toCursorPageResponse(slice);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponsePlayListDto findAllByUserId(Long userId, LocalDateTime cursor,
      Pageable pageable) {
    Slice<Playlist> slice = playlistRepository.findAllByUserId(userId, cursor, pageable);

    return toCursorPageResponse(slice);
  }


  private CursorPageResponsePlayListDto toCursorPageResponse(Slice<Playlist> slice) {
    List<PlaylistSlimDto> slimDtos = slice.getContent().stream()
        .map(playlist -> {
          // PlaylistUtil을 사용해 각 플레이리스트의 요약(title) 생성
          String summary = PlaylistUtil.summary(playlist.getItems());
          // Playlist와 생성된 summary를 사용해 DTO 만들기
          return PlaylistSlimDto.from(playlist, summary);
        })
        .toList();

    LocalDateTime nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1)
          .getCreatedAt();
    }

    return CursorPageResponsePlayListDto.builder()
        .content(slimDtos)
        .nextCursor(nextCursor)
        .size(slice.getNumberOfElements())
        .hasNext(slice.hasNext())
        .build();
  }
}
