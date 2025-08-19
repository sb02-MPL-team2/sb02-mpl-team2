package com.codeit.sb02mplteam2.domain.playlist.batch;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import com.codeit.sb02mplteam2.domain.playlist.entity.Subscribe;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaylistProcessorTest {

  private User user;
  private String title;
  private String description;

  @BeforeEach
  void setUp() {
    user = new User();
    title = "테스트용 플리";
    description = "테스트 목적";
  }

  @Test
  @DisplayName("플리를 점수 객체로 변환")
  void process() throws Exception {
    //given
    PlaylistProcessor playlistProcessor = new PlaylistProcessor();

    Playlist playlist = new Playlist(user, title, description);
    playlist.getSubscribes().add(new Subscribe());
    //when
    PlaylistSubscriberHistory result = playlistProcessor.process(playlist);

    //then
    assertAll(
        () -> assertEquals(user, result.getPlaylist().getUser()),
        () -> assertEquals(title, result.getPlaylist().getTitle()),
        () -> assertEquals(description, result.getPlaylist().getDescription()),
        () -> assertEquals(1, result.getCount()));
  }
}