package com.codeit.sb02mplteam2.domain.subscribe.service;

import static com.codeit.sb02mplteam2.util.PlaylistUtil.toPlaylistItemDtoList;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toResponseDto;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toUserSlimDto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.subscribe.SubscribeEventPublisher;
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
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicSubscribeService implements SubscribeService{

  private final UserRepository userRepository;
  private final SubscribeRepository subscribeRepository;
  private final PlaylistRepository playlistRepository;
  private final SubscribeEventPublisher subscribeEventPublisher;

  @Override
  @Transactional
  @CachePut(value = "playlists", key = "#request.playlistId")
  public PlaylistDto subscribe(Long userId, SubscribeRequest request) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));

    Long playlistId = request.playlistId();
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    // 구독 중복 체크
    Subscribe existingSubscribe = subscribeRepository.findByUserAndPlaylist(user, playlist)
        .orElse(null);
    if (existingSubscribe != null) {
      log.warn("이미 구독된 플레이리스트입니다. user id = {}, name = {}, playlist id = {}, playlist Title = {}",
          userId,
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
        subscribeEventPublisher.sendEvent(userId, playlist.getUser().getId(), playlistId);
      } else {
        log.warn("구독 실패 user id = {}, name = {}, playlist id = {}, playlist Title = {}", userId,
            user.getUsername(), playlistId, playlist.getTitle());
      }
    }
    playlistRepository.save(playlist);

    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

  @Override
  @Transactional
  @CachePut(value = "playlists", key = "#request.playlistId")
  public PlaylistDto unSubscribe(Long userId, SubscribeRequest request) {
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
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

}
