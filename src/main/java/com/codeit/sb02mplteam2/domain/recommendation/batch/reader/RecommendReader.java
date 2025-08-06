package com.codeit.sb02mplteam2.domain.recommendation.batch.reader;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class RecommendReader implements ItemReader<PlaylistItem> {

  private final PlaylistItemRepository playlistItemRepository;
  private int index = 0;
  private List<PlaylistItem> data;

  @Override
  public PlaylistItem read() {
    if (data == null) {
      data = playlistItemRepository.findAll();
    }
    return index < data.size() ? data.get(index++) : null;
  }
}
