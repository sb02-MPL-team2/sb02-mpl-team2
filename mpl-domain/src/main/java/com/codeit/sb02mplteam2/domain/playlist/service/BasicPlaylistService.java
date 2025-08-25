package com.codeit.sb02mplteam2.domain.playlist.service;

import static com.codeit.sb02mplteam2.util.PlaylistUtil.toPlaylistItemDtoList;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toResponseDto;
import static com.codeit.sb02mplteam2.util.PlaylistUtil.toUserSlimDto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.SubscribeRepository;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import com.codeit.sb02mplteam2.util.RabbitConst;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicPlaylistService implements PlaylistService {

  private final UserRepository userRepository;
  private final PlaylistRepository playlistRepository;
  private final SubscribeRepository subscribeRepository;
  private final FollowRepository followRepository;
  private final ApplicationEventPublisher eventPublisher;

  private final RabbitTemplate rabbitTemplate;

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
    boolean success = playlist.subscribe(subscribe);
    if (success) {
      log.info("플레이리스트 생성 성공 user id = {}, name = {}", userId, user.getUsername());
    } else {
      log.warn("플레이리스트 생성 실패 user id = {}, name = {}", userId, user.getUsername());
    }
    playlistRepository.save(playlist);

    //이벤트 발행
    Set<Long> followersId = followRepository.findAllFollowersIdByToUserId(userId);
    log.info("{}의 팔로워에게 알람 전송 ", followersId.size());
    BulkNotificationEvent event = new BulkNotificationEvent(followersId,
        NotificationType.NEW_PLAYLIST_BY_FOLLOWING,
        playlist.getId(), userId);
//    eventPublisher.publishEvent();
    rabbitTemplate.convertAndSend(RabbitConst.notificationExchange,RabbitConst.Notification_Bulk_Send_RoutingKey,event);
    log.info("전송 완료");
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList);
  }

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
        //이벤트 발행
        NotificationEvent event = new NotificationEvent(playlist.getUser().getId(),
            NotificationType.PLAYLIST_SUBSCRIBED,
            playlist.getId(), userId);
//      eventPublisher.publishEvent(event);
        rabbitTemplate.convertAndSend(RabbitConst.notificationExchange,RabbitConst.Playlist_Send_Notification_RoutingKey,event);
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

  @Override
  @Transactional
  @CacheEvict(value = "playlists", key = "#playlistId")
  public void delete(Long playlistId, Long userId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    if (!playlist.getUser().getId().equals(userId)) {
      throw new PlaylistException(ErrorCode.UNAUTHORIZED);
    }

    playlistRepository.delete(playlist);
  }

  @Override
  @Transactional
  @CachePut(value = "playlists", key = "#playlistId")
  public PlaylistDto update(Long userId,Long playlistId, PlaylistUpdateRequest request) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
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
