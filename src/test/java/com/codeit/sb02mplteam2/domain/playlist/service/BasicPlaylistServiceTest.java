package com.codeit.sb02mplteam2.domain.playlist.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistCreateRequest;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistUpdateRequest;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicPlaylistServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlaylistRepository playlistRepository;

  @InjectMocks
  private BasicPlaylistService playlistService;

  private User user;
  private Playlist playlist;
  private final String title = "title";
  private final String description = "description";

  @BeforeEach
  void setUp() {
    user = new User();
    playlist = new Playlist(user, title, description);
  }

  @Test
  void create() {
    //given
    Long mockUserId = 1L;
    PlaylistCreateRequest request = new PlaylistCreateRequest(mockUserId, title, description);
    when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

    //when
    PlaylistDto playlistDto = playlistService.create(request);
    //then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertEquals(description, playlistDto.description())
    );
  }

  @Test
  void createWithEmptyDescription() {
    //given
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    PlaylistCreateRequest request = new PlaylistCreateRequest(1L, title, null);

    //when
    PlaylistDto playlistDto = playlistService.create(request);

    //then
    assertAll(
        () -> assertEquals(title, playlistDto.title()),
        () -> assertNull(playlistDto.description())
    );
  }

  @Test
  void update() {
    String newTitle = "newTitle";
    String newDescription = "newDescription";

    PlaylistUpdateRequest request = new PlaylistUpdateRequest(newTitle, newDescription);
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    PlaylistDto playlistDto = playlistService.update(1L, request);

    assertAll(
        () -> assertEquals(newTitle, playlistDto.title()),
        () -> assertEquals(newDescription, playlistDto.description())
    );
  }

  @Test
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
    playlistService.delete(1L);

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