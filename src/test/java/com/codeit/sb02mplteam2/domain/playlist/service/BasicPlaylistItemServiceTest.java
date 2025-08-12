package com.codeit.sb02mplteam2.domain.playlist.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicPlaylistItemServiceTest {

  @Mock
  private PlaylistItemRepository playlistItemRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

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
    ReflectionTestUtils.setField(user, "id", 1L);
    playlist = new Playlist(user, title, description);
  }

  @Test
  @DisplayName("플레이리스트에 콘텐츠가 성공적으로 추가되어야 한다.")
  void addContent() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    Content content = new Content("테스트", ContentCategory.MOVIE);
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content));

    PlaylistDto playlistDto = playlistItemService.addContent(1L, 1L, 1L);

    assertAll(
        () -> assertEquals(1, playlistDto.items().size())
    );
  }

  @Test
  @DisplayName("플레이리스트에 다수의 콘텐츠가 성공적으로 추가되어야 한다.")
  void addContentList() {
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));

    Content content1 = new Content("테스트1", ContentCategory.MOVIE);
    Content content2 = new Content("테스트2", ContentCategory.MOVIE);
    Content content3 = new Content("테스트3", ContentCategory.MOVIE);
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));
    when(contentRepository.findById(3L)).thenReturn(Optional.of(content3));
    PlaylistDto playlistDto = playlistItemService.addContentList(1L, 1L, List.of(1L, 2L, 3L));
    assertAll(
        () -> assertEquals(3, playlistDto.items().size())
    );
  }

  @Test
  @DisplayName("콘텐츠 목록이 존재하는 플레이리스트에 콘텐츠가 성공적으로 추가되어야 한다.")
  void insertContent() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    Content content1 = mock(Content.class);
    when(content1.getId()).thenReturn(1L);
    when(content1.getCategory()).thenReturn(ContentCategory.TV);
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));
    //1차적으로 값 주입
    PlaylistDto playlistDto1 = playlistItemService.addContent(1L, 1L, 1L);
    //1차 값 증명
    assertAll(
        () -> assertEquals(1, playlistDto1.items().size())
    );

    //2차 값 주입
    Content content2 = new Content("테스트1", ContentCategory.MOVIE);
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));

    PlaylistDto playlistDto2 = playlistItemService.addContent(1L, 1L,2L);
    assertAll(
        () -> assertEquals(2, playlistDto2.items().size())
    );
  }

  @Test
  @DisplayName("콘텐츠 목록이 존재하는 플레이리스트에 다수의 콘텐츠가 성공적으로 추가되어야 한다.")
  void insertContentList() {
    //given
    when(playlistRepository.findById(1L)).thenReturn(Optional.of(playlist));
    Content content1 = new Content("테스트", ContentCategory.MOVIE);
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content1));

    //1차적으로 값 주입
    PlaylistDto playlistDto1 = playlistItemService.addContent(1L, 1L, 1L);

    //1차 값 증명
    assertAll(
        () -> assertEquals(1, playlistDto1.items().size())
    );

    //2차 값 주입
    Content content2 = new Content("테스트2", ContentCategory.MOVIE);
    Content content3 = new Content("테스트3", ContentCategory.MOVIE);
    when(contentRepository.findById(2L)).thenReturn(Optional.of(content2));
    when(contentRepository.findById(3L)).thenReturn(Optional.of(content3));

    PlaylistDto playlistDto2 = playlistItemService.addContentList(1L, 1L, List.of(2L, 3L));
    assertAll(
        () -> assertEquals(3, playlistDto2.items().size())
    );
  }
}