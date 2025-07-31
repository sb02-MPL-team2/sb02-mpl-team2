package com.codeit.sb02mplteam2.domain.playlist.service;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.exception.MplException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BasicPlaylistItemService implements PlaylistItemService {

  private final PlaylistItemRepository playlistItemRepository;
  private final PlaylistRepository playlistRepository;
  private final ContentRepository contentRepository;

  /*
  TODO 1. 이미 넣어진 Content는 넣지말아야함
  TODO 2. Content 목록 정렬 기능 추가해야함
   */

  @Override
  public PlaylistDto addContent(Long playlistId, Long contentId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new MplException("PlayList를 찾을 수 없습니다.")
    );
    //중복 검사 로직
//    boolean isExist = playlist.getItems().stream()
//        .anyMatch(item -> item.getContent().getId().equals(contentId));
    //TODO 현재 Content에 Get메서드 없어서 임시로 false 처리
    boolean isExist = false;

    if (isExist) {
      log.warn("콘텐츠(id:{})는 이미 플레이리스트(id:{})에 존재합니다.", contentId, playlistId);
      return PlaylistDto.from(playlist);
    }

    Content content = contentRepository.findById(contentId).orElseThrow(
        () -> new MplException("Content를 찾을 수 없습니다.")
    );
    int size = playlist.getItems().size();

    PlaylistItem playlistItem = new PlaylistItem(size, content);
    playlistItemRepository.save(playlistItem);

    playlist.addItem(playlistItem);
    playlistRepository.save(playlist);


    return PlaylistDto.from(playlist);
  }

  @Override
  public PlaylistDto addContentList(Long playlistId, List<Long> contentIds) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new MplException("PlayList를 찾을 수 없습니다.")
    );
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
    return PlaylistDto.from(playlist);
  }

  @Override
  public void deleteAllByPlaylistId(Long playlistId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new MplException("PlayList를 찾을 수 없습니다.")
    );
    playlistItemRepository.deleteAll(playlist.getItems());
    playlist.getItems().clear();
    playlistRepository.save(playlist);
  }

  //개인 플레이리스트에서 콘텐츠 삭제
  @Override
  public void deleteByContentId(Long playlistId, Long contentId) {
    Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(
        () -> new MplException("PlayList를 찾을 수 없습니다.")
    );

//    Optional<PlaylistItem> target = playlist.getItems().stream().filter(
//        item -> item.getContent().getId().equals(contentId)
//    ).findFirst();

    //TODO 현재 Content에 Get메서드 없어서 임시로 empty 처리
    Optional<PlaylistItem> target = Optional.empty();

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
