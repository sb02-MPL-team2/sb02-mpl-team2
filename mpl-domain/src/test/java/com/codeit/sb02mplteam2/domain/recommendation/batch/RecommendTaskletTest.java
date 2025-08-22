package com.codeit.sb02mplteam2.domain.recommendation.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.recommendation.repository.PlaylistScoreRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class RecommendTaskletTest {

  @Mock
  private PlaylistScoreRepository playlistScoreRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private RecommendTasklet recommendTasklet;

  private Playlist playlist1;
  private Playlist playlist2;

  @BeforeEach
  void setUp() {
    User user = new User();

    playlist1 = new Playlist(user, "Top Playlist 1", "Desc 1");
    playlist2 = new Playlist(user, "Top Playlist 2", "Desc 2");
  }
/*
  @Test
  @DisplayName("상위 플레이리스트가 존재할 경우, 각 플레이리스트에 대해 브로드캐스트 이벤트를 발행해야 한다")
  @Disabled

  void execute_shouldPublishEvents_whenTopPlaylistsExist() throws Exception {
    //Given
    PlaylistScore score1 = new PlaylistScore(playlist1, 95.5);
    PlaylistScore score2 = new PlaylistScore(playlist2, 90.0);
    List<PlaylistScore> topPlaylists = List.of(score1, score2);

    //When
    when(playlistScoreRepository.findTop3ByCreatedAtBetweenOrderByScoreDesc(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(topPlaylists);

    RepeatStatus status = recommendTasklet.execute(null, null);
    //Then
    verify(eventPublisher, times(2)).publishEvent(any(BroadcastEvent.class));

    ArgumentCaptor<BroadcastEvent> eventCaptor = ArgumentCaptor.forClass(BroadcastEvent.class);
    verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());

    List<BroadcastEvent> capturedEvents = eventCaptor.getAllValues();
    assertEquals(playlist1.getId(), capturedEvents.get(0).getTargetId());
    assertEquals(playlist2.getId(), capturedEvents.get(1).getTargetId());

    assertEquals(RepeatStatus.FINISHED, status);
  }

   */

  @Test
  @DisplayName("상위 플레이리스트가 존재하지 않을 경우, 이벤트를 발행하지 않아야 한다")
  void execute_shouldNotPublishEvents_whenNoPlaylistsExist() throws Exception {
    // Given
    when(playlistScoreRepository.findTop3ByCreatedAtBetweenOrderByScoreDesc(any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(Collections.emptyList());

    // When
    RepeatStatus status = recommendTasklet.execute(null, null);

    // Then
    // 이벤트 발행기가 한 번도 호출되지 않았는지 확인합니다.
    verify(eventPublisher, never()).publishEvent(any());
    assertEquals(RepeatStatus.FINISHED, status);
  }
}