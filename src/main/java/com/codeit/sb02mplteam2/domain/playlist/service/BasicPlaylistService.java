package com.codeit.sb02mplteam2.domain.playlist.service;

import static com.codeit.sb02mplteam2.domain.playlist.service.PlaylistUtil.toResponseDto;

import com.codeit.sb02mplteam2.domain.content.dto.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.CursorPageResponsePlayListDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.SubscribeRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import com.codeit.sb02mplteam2.exception.user.UserException;
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
public class BasicPlaylistService implements PlaylistService{

  private final UserRepository userRepository;
  private final PlaylistRepository playlistRepository;
  private final SubscribeRepository subscribeRepository;

  @Override
  @Transactional
  public PlaylistDto create(PlaylistCreateRequest request) {
    Long userId = request.userId();
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));
    Playlist playlist = new Playlist(user, request.title(), request.description());

    //구독 생성
    Subscribe subscribe = new Subscribe(user, playlist);
    subscribeRepository.save(subscribe);

    //자기자신 구독 추가
    boolean success = playlist.subscribe(subscribe);
    if (success) {
      log.info("플레이리스트 생성 성공 user id = {}, name = {}", userId, user.getUsername());
    } else {
      log.warn("플레이리스트 생성 실패 user id = {}, name = {}", userId, user.getUsername());

    }
    playlistRepository.save(playlist);

    return PlaylistDto.from(playlist);
  }

  @Override
  @Transactional
  public PlaylistDto subscribe(SubscribeRequest request) {
    Long userId = request.userId();
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));

    Long playlistId = request.playlistId();
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    // 구독 중복 체크
    Subscribe existingSubscribe = subscribeRepository.findByUserAndPlaylist(user, playlist)
        .orElse(null);
    if (existingSubscribe != null) {
      log.warn("이미 구독된 플레이리스트입니다. user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
          user.getUsername(), playlistId, playlist.getTitle());
    } else {
      //구독 생성
      Subscribe subscribe = new Subscribe(user, playlist);
      subscribeRepository.save(subscribe);

      //구독 추가
      boolean success = playlist.subscribe(subscribe);
      if (success) {
        log.info("구독 성공 user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
            user.getUsername(), playlistId, playlist.getTitle());
      } else {
        log.warn("구독 실패 user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
            user.getUsername(), playlistId, playlist.getTitle());
      }
    }
    playlistRepository.save(playlist);
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    return PlaylistDto.from(playlist, responseDto);
  }

  @Override
  @Transactional
  public PlaylistDto unSubscribe(SubscribeRequest request) {
    Long userId = request.userId();
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));

    Long playlistId = request.playlistId();
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    Subscribe subscribe = subscribeRepository.findByUserAndPlaylist(user, playlist).orElseThrow(
        () -> new PlaylistException(ErrorCode.SUBSCRIBE_NOT_FOUND)
    );

    boolean success = playlist.unSubscribe(subscribe);
    if (success) {
      log.info("구독 취소 성공 user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
          user.getUsername(), playlistId, playlist.getTitle());
      subscribeRepository.delete(subscribe);
      playlistRepository.save(playlist);
    } else {
      log.warn("구독 취소 실패 user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
          user.getUsername(), playlistId, playlist.getTitle());
    }
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    return PlaylistDto.from(playlist, responseDto);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    playlistRepository.delete(playlist);
  }

  @Override
  @Transactional
  public PlaylistDto update(Long id, PlaylistUpdateRequest request) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    playlist.update(request.newTitle(), request.newDescription());
    playlistRepository.save(playlist);
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    return PlaylistDto.from(playlist, responseDto);
  }

  @Override
  @Transactional(readOnly = true)
  public PlaylistDto findById(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    return PlaylistDto.from(playlist, responseDto);
  }
}
