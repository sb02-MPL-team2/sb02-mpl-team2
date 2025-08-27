package com.codeit.sb02mplteam2.domain.playlist.batch;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistSubscriberHistoryRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
@Disabled
class PlaylistHistoryWriterTest {

  @Mock
  private PlaylistSubscriberHistoryRepository playlistSubscriberHistoryRepository;

  @InjectMocks
  private PlaylistHistoryWriter playlistHistoryWriter;

  @Test
  @DisplayName("청크 단위 모든 플리 객체 저장해야함")
  void write_SavesAllItemsInChunk() throws Exception {
    //given
    User user = new User();
    Playlist playlist1 = new Playlist(user, "플리1", "설명1");
    Playlist playlist2 = new Playlist(user, "플리2", "설명2");

    PlaylistSubscriberHistory history1 = new PlaylistSubscriberHistory(playlist1,
        10);
    PlaylistSubscriberHistory history2 = new PlaylistSubscriberHistory(playlist2,
        20);

    List<PlaylistSubscriberHistory> items = List.of(history1, history2);

    Chunk<PlaylistSubscriberHistory> chunk = new Chunk<>(items);

    //when
    playlistHistoryWriter.write(chunk);

    //then
    verify(playlistSubscriberHistoryRepository, times(1)).saveAll(items);

    // ArgumentCaptor는 Mock 객체의 메서드에 전달된 인자를 "캡처"하여 검사할 수 있게 해주는 도구
    ArgumentCaptor<List<PlaylistSubscriberHistory>> captor = ArgumentCaptor.forClass(List.class);

    verify(playlistSubscriberHistoryRepository).saveAll(captor.capture()); // 인자 캡처
    List<PlaylistSubscriberHistory> capturedList = captor.getValue(); // 캡처된 인자 가져오기

    assertAll(
        () -> assertEquals(2, capturedList.size()),
        () -> assertEquals(history1, capturedList.get(0)),
        () -> assertEquals(history2, capturedList.get(1))
    );

  }
}