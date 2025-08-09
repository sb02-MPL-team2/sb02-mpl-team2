package com.codeit.sb02mplteam2.domain.playlist.service;

import static com.codeit.sb02mplteam2.domain.playlist.service.PlaylistUtil.toPlaylistItemDtoList;
import static com.codeit.sb02mplteam2.domain.playlist.service.PlaylistUtil.toResponseDto;
import static com.codeit.sb02mplteam2.domain.playlist.service.PlaylistUtil.toUserSlimDto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistItemDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.content.ContentException;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicPlaylistItemService implements PlaylistItemService {

  private final PlaylistItemRepository playlistItemRepository;
  private final PlaylistRepository playlistRepository;
  private final ContentRepository contentRepository;

  /*
  TODO. Content 목록 정렬 기능 추가해야함
   */

  @Override
  @Transactional
  public PlaylistDto addContent(Long playlistId, Long contentId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    //중복 검사 로직
    boolean isExist = playlist.getItems().stream()
        .anyMatch(item -> item.getContent().getId().equals(contentId));

    if (isExist) {
      log.warn("콘텐츠(id:{})는 이미 플레이리스트(id:{})에 존재합니다.", contentId, playlistId);
      UserSlimDto userSlimDto = toUserSlimDto(playlist);
      List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
      return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList);
    }

    Content content = contentRepository.findById(contentId).orElseThrow(
        () -> new ContentException(ErrorCode.CONTENT_NOT_FOUND)
    );
    int size = playlist.getItems().size();

    PlaylistItem playlistItem = new PlaylistItem(size, content);
    playlistItemRepository.save(playlistItem);

    playlist.addItem(playlistItem);
    playlistRepository.save(playlist);

    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

  @Override
  @Transactional
  public PlaylistDto addContentList(Long playlistId, List<Long> contentIds) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
    List<Content> contentList = new ArrayList<>();
    for (Long contentId : contentIds) {
      contentRepository.findById(contentId).ifPresent(contentList::add);
    }

    int size = playlist.getItems().size();
    int length = contentList.size();
    int end = size + length;

    List<PlaylistItem> playlistItems = new ArrayList<>();
    for (; size < end; size++) {
      PlaylistItem playlistItem = new PlaylistItem(size, contentList.get(size - playlist.getItems().size()));
      playlist.addItem(playlistItem);
      playlistItems.add(playlistItem);
    }

    playlistItemRepository.saveAll(playlistItems);
    playlistRepository.save(playlist);

    List<ContentResponseDto> responseDto = toResponseDto(playlist.getItems());
    UserSlimDto userSlimDto = toUserSlimDto(playlist);
    List<PlaylistItemDto> playlistItemDtoList = toPlaylistItemDtoList(playlist);
    return PlaylistDto.of(playlist, userSlimDto, playlistItemDtoList, responseDto);
  }

  @Override
  @Transactional
  public void deleteAllByPlaylistId(Long playlistId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    playlistItemRepository.deleteAll(playlist.getItems());
    playlist.getItems().clear();
    playlistRepository.save(playlist);
  }

  //개인 플레이리스트에서 콘텐츠 삭제
  @Override
  @Transactional
  public void deleteByContentId(Long playlistId, Long contentId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));

    Optional<PlaylistItem> target = playlist.getItems().stream().filter(
        item -> item.getContent().getId().equals(contentId)
    ).findFirst();

    if (target.isEmpty()) {
      log.warn("플레이리스트(id:{})에 존재하지 않는 콘텐츠(id:{})의 삭제가 요청되었습니다.", playlistId, contentId);
      return;
    }

    PlaylistItem itemToRemove = target.get();
    playlist.getItems().remove(itemToRemove);

    //순서 재정렬
    List<PlaylistItem> remainingItems = playlist.getItems();
    for (int i = 0; i < remainingItems.size(); i++) {
      remainingItems.get(i).setOrderIndex(i);
    }

    playlistRepository.save(playlist);
  }
}
