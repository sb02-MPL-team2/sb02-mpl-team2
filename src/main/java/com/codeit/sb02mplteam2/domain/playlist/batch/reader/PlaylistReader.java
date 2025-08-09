package com.codeit.sb02mplteam2.domain.playlist.batch.reader;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class PlaylistReader implements ItemReader<Playlist> {

  private final PlaylistRepository playlistRepository;
  private int pageDataIndex = 0;
  private int pageNumber = 0;
  private final int pageSize = 1000;
  private List<Playlist> data;

  @Override
  public Playlist read() {
    if (data == null || pageDataIndex >= data.size()) {
      Pageable pageable = PageRequest.of(pageNumber, pageSize);
      data = playlistRepository.findAll(pageable).getContent();
      pageNumber++;
      pageDataIndex = 0;
      if (data.isEmpty()) {
        return null;
      }
    }
    return data.get(pageDataIndex++);
  }
}
