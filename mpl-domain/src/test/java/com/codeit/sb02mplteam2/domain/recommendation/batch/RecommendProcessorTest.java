package com.codeit.sb02mplteam2.domain.recommendation.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistSubscriberHistoryRepository;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RecommendProcessorTest {
  @Mock
  private PlaylistSubscriberHistoryRepository historyRepository;

  @InjectMocks
  private RecommendProcessor recommendProcessor;

  private final LocalDateTime testStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);
  private final LocalDateTime testEndDate = LocalDateTime.of(2024, 1, 8, 0, 0);

  private Playlist playlist;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    playlist = new Playlist(user, "Test Playlist", "Description");

    ReflectionTestUtils.setField(recommendProcessor, "startDate", testStartDate);
    ReflectionTestUtils.setField(recommendProcessor, "endDate", testEndDate);
  }

  @Test
  @DisplayName("히스토리가 4개 미만이고 비어있지 않으면 단순 평균 반환")
  void process_lessThanFourHistories_notEmpty() throws Exception {
    // Given
    LocalDateTime now = LocalDateTime.now();
    List<PlaylistSubscriberHistory> histories = List.of(
        new PlaylistSubscriberHistory(playlist, 10),
        new PlaylistSubscriberHistory(playlist, 20),
        new PlaylistSubscriberHistory(playlist, 30)
    );
    when(historyRepository.findByPlaylistAndCreatedAtBetween(playlist, testStartDate, testEndDate))
        .thenReturn(histories);

    // When
    PlaylistScore result = recommendProcessor.process(playlist);

    // Then
    assertNotNull(result);
    assertEquals(20.0, result.getScore());
    assertEquals(playlist, result.getPlaylist());
  }

  @Test
  @DisplayName("히스토리가 4개 미만이고 비어있으면 null 반환")
  void process_lessThanFourHistories_empty() throws Exception {
    // Given
    when(historyRepository.findByPlaylistAndCreatedAtBetween(playlist, testStartDate, testEndDate))
        .thenReturn(Collections.emptyList());

    // When
    PlaylistScore result = recommendProcessor.process(playlist);

    // Then
    assertNull(result);
  }

  @Test
  @DisplayName("히스토리가 4개 이상이면 이상치 제거 후 평균 반환")
  void process_fourOrMoreHistories() throws Exception {
    // Given
    List<PlaylistSubscriberHistory> histories = List.of(
        new PlaylistSubscriberHistory(playlist, 10),
        new PlaylistSubscriberHistory(playlist, 20),
        new PlaylistSubscriberHistory(playlist, 30),
        new PlaylistSubscriberHistory(playlist, 40),
        new PlaylistSubscriberHistory(playlist, 50),
        new PlaylistSubscriberHistory(playlist, 60),
        new PlaylistSubscriberHistory(playlist, 1000));
    when(historyRepository.findByPlaylistAndCreatedAtBetween(playlist, testStartDate, testEndDate))
        .thenReturn(histories);

    // When
    PlaylistScore result = recommendProcessor.process(playlist);

    // Then
    assertNotNull(result);
    assertEquals(35.0, result.getScore());
    assertEquals(playlist, result.getPlaylist());
  }
}