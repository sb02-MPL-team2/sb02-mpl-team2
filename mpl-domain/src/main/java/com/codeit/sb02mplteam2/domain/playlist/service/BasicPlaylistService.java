package com.codeit.sb02mplteam2.domain.playlist.service;

import static com.codeit.sb02mplteam2.util.PlaylistUtil.toPlaylistItemDtoList;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toResponseDto;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toUserSlimDto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.PlaylistCacheManager;
import com.codeit.sb02mplteam2.domain.playlist.PlaylistEventPublisher;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.subscribe.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.subscribe.repository.SubscribeRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicPlaylistService implements PlaylistService {

  private final UserRepository userRepository;
  private final PlaylistRepository playlistRepository;
  private final SubscribeRepository subscribeRepository;
  private final PlaylistEventPublisher playlistEventPublisher;
  private final PlaylistCacheManager playlistCacheManager;

  @Override
  @Transactional
  public PlaylistDto create(Long userId, PlaylistCreateRequest request) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));
    Playlist playlist = new Playlist(user, request.title(), request.description());

    //구독 생성
    Subscribe subscribe = new Subscribe(user, playlist);
    subscribeRepository.save(subscribe);

    //자기자신 구독 추가
    playlist.subscribe(subscribe);
    playlistRepository.save(playlist);

    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    PlaylistDto playlistDto = PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList,
        responseDto);

    //캐시 적용
    playlistCacheManager.evictAndPut(playlistDto);

    playlistEventPublisher.sendEvent(userId, playlist.getId());
    return playlistDto;
  }

  @Override
  @Transactional
  @CacheEvict(value = "playlists", key = "#id")
  public void delete(Long id, Long userId) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    if (!playlist.getUser().getId().equals(userId)) {
      throw new PlaylistException(ErrorCode.UNAUTHORIZED);
    }

    playlistRepository.delete(playlist);
  }

  @Override
  @Transactional
  @CachePut(value = "playlists", key = "#id")
  public PlaylistDto update(Long userId,Long id, PlaylistUpdateRequest request) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    //동일인 확인
    if (!playlist.getUser().getId().equals(userId)) {
      throw new PlaylistException(ErrorCode.UNAUTHORIZED);
    }
    playlist.update(request.newTitle(), request.newDescription());
    playlistRepository.save(playlist);
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "playlists", key = "#id")
  public PlaylistDto findById(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

  @Override
  @Transactional(readOnly = true)
  @CachePut(value = "playlists", key = "#id")
  public PlaylistDto refreshAndFindById(Long id) {
    Playlist playlist = playlistRepository.findById(id).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }
}
