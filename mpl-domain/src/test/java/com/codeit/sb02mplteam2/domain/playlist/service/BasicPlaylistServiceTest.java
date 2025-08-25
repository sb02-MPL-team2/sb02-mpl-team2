package com.codeit.sb02mplteam2.domain.playlist.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.request.SubscribeRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.SubscribeRepository;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicPlaylistServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlaylistRepository playlistRepository;

  @Mock
  private FollowRepository followRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private SubscribeRepository subscribeRepository;

  @InjectMocks
  private BasicPlaylistService playlistService;

  private User user;
  private Playlist playlist;
  private Subscribe subscribe;
  private final String title = "summary";
  private final String description = "description";

  @BeforeEach
  void setUp() {
    user = new User();
    ReflectionTestUtils.setField(user, "id", 1L);
    playlist = new Playlist(user, title, description);
    subscribe = new Subscribe(user, playlist);
    playlist.subscribe(subscribe);
  }

  @Test
  @DisplayName("플레이리스트 생성 성공 테스트")
  @Disabled
  void create() {
    //given
    Long mockUserId = 1L;
    PlaylistCreateRequest request = new PlaylistCreateRequest(title, description);
    when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

    //when
    PlaylistDto playlistDto = playlistService.create(mockUserId, request);
    //then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description()),
        () -> assertEquals(1, playlistDto.subscriberCount())
    );
  }

  @Test
  @DisplayName("설명 없이 플레이리스트 생성 성공 테스트")
  @Disabled
  void createWithEmptyDescription() {
    //given
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    PlaylistCreateRequest request = new PlaylistCreateRequest( title, null);

    //when
    PlaylistDto playlistDto = playlistService.create(1L, request);

    //then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertNull(playlistDto.description()),
        () -> assertEquals(1, playlistDto.subscriberCount())
    );
  }

  @Test
  @DisplayName("구독 성공 테스트")
  @Disabled
  void subscribe() {
    // given
    User newUser = new User();
    when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    SubscribeRequest request = new SubscribeRequest(1L);
    // when
    PlaylistDto playlistDto = playlistService.subscribe(1L, request);

    // then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description()),
        () -> assertEquals(2, playlistDto.subscriberCount())
    );
  }

  @Test
  @DisplayName("구독 취소 성공 테스트")
  @Disabled
  void unSubscribe() {
    // given
    User newUser = new User();
    Subscribe newSubscribe = new Subscribe(newUser, playlist);
    playlist.subscribe(newSubscribe);

    when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    when(subscribeRepository.findByUserAndPlaylist(newUser, playlist)).thenReturn(
        Optional.of(newSubscribe));

    SubscribeRequest request = new SubscribeRequest( 1L);
    // when
    PlaylistDto playlistDto = playlistService.unSubscribe(1L, request);
    // then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description()),
        () -> assertEquals(1, playlistDto.subscriberCount())
    );
  }

  @Test
  @DisplayName("자기 자신 구독 취소 실패 테스트")
  void unSubscribeWithMeMustFailed() {
    // given
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    when(subscribeRepository.findByUserAndPlaylist(user, playlist)).thenReturn(
        Optional.of(subscribe));
    SubscribeRequest request = new SubscribeRequest(1L);
    // when
    PlaylistDto playlistDto = playlistService.unSubscribe(1L, request);
    // then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description()),
        () -> assertEquals(1, playlistDto.subscriberCount())
    );
  }

  @Test
  @DisplayName("업데이트 성공 테스트")
  void update() {
    String newTitle = "newTitle";
    String newDescription = "newDescription";

    PlaylistUpdateRequest request = new PlaylistUpdateRequest(newTitle, newDescription);
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    PlaylistDto playlistDto = playlistService.update(1L, 1L, request);

    assertAll(
        () -> assertEquals(newTitle, playlistDto.title()),
        () -> assertEquals(newDescription, playlistDto.description())
    );
  }

  @Test
  @DisplayName("플레이리스트 단건 조회 테스트")
  void findById() {
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    PlaylistDto playlistDto = playlistService.findById(1L);
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description())
    );
  }

  @Test
  void delete() {
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    playlistService.delete(1L, 1L);

    verify(playlistRepository).findById(1L);
    verify(playlistRepository).delete(playlist);

  }

  @Test
  void findAllByContentId() {
  }

  @Test
  void findAllByUserId() {
  }
}