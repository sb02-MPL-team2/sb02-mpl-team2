package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicPlaylistService implements PlaylistService{

  private final UserRepository userRepository;
  private final PlaylistRepository playlistRepository;

  @Override
  public PlaylistDto create(PlaylistCreateRequest request) {
    Long userId = request.userId();
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));
    Playlist playlist = new Playlist(user, request.title(), request.description());
    playlistRepository.save(playlist);

    return PlaylistDto.from(playlist);
  }

  @Override
  public PlaylistDto update(Long id, PlaylistUpdateRequest request) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    playlist.update(request.newTitle(), request.newDescription());
    playlistRepository.save(playlist);
    return PlaylistDto.from(playlist);
  }

  @Override
  public void delete(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    playlistRepository.delete(playlist);
  }

  @Override
  public PlaylistDto findById(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    return PlaylistDto.from(playlist);
  }

  @Override
  public CursorPageResponsePlayListDto findAllByContentId(Long contentId, LocalDateTime cursor,
      Pageable pageable) {
    return null;
  }

  @Override
  public CursorPageResponsePlayListDto findAllByUserId(Long userId, LocalDateTime cursor,
      Pageable pageable) {
    Slice<Playlist> slice = playlistRepository.findAllByUserId(userId, cursor, pageable);

    LocalDateTime nextCursor = null;
    if (!slice.getContent().isEmpty()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1)
          .getCreatedAt();
    }

    return CursorPageResponsePlayListDto.of(slice, nextCursor);
  }
}
