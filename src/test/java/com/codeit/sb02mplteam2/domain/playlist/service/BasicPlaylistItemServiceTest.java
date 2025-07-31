package com.codeit.sb02mplteam2.domain.playlist.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicPlaylistItemServiceTest {

  @Mock
  private PlaylistItemRepository playlistItemRepository;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private PlaylistRepository playlistRepository;

  @InjectMocks
  private BasicPlaylistItemService playlistItemService;

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
  void addContent() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    Content content = new Content();
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

    PlaylistDto playlistDto = playlistItemService.addContent(1L, 1L);

    assertAll(
        () -> assertEquals(1, playlistDto.items().size())
    );
  }

  @Test
  void addContentList() {
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    Content content1 = new Content();
    Content content2 = new Content();
    Content content3 = new Content();
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));
    when(contentRepository.findById(3L)).thenReturn(Optional.of(content3));
    PlaylistDto playlistDto = playlistItemService.addContentList(1L, List.of(1L, 2L, 3L));
    assertAll(
        () -> assertEquals(3, playlistDto.items().size())
    );
  }

  @Test
  void insertContent() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    Content content1 = new Content();
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));
    //1차적으로 값 주입
    PlaylistDto playlistDto1 = playlistItemService.addContent(1L, 1L);
    //1차 값 증명
    assertAll(
        () -> assertEquals(1, playlistDto1.items().size())
    );

    //2차 값 주입
    Content content2 = new Content();
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));

    PlaylistDto playlistDto2 = playlistItemService.addContent(1L,2L);
    assertAll(
        () -> assertEquals(2, playlistDto2.items().size())
    );
  }

  @Test
  void insertContentList() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    Content content1 = new Content();
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));

    //1차적으로 값 주입
    PlaylistDto playlistDto1 = playlistItemService.addContent(1L, 1L);

    //1차 값 증명
    assertAll(
        () -> assertEquals(1, playlistDto1.items().size())
    );

    //2차 값 주입
    Content content2 = new Content();
    Content content3 = new Content();
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));
    when(contentRepository.findById(3L)).thenReturn(Optional.of(content3));

    PlaylistDto playlistDto2 = playlistItemService.addContentList(1L, List.of(2L, 3L));
    assertAll(
        () -> assertEquals(3, playlistDto2.items().size())
    );
  }
}