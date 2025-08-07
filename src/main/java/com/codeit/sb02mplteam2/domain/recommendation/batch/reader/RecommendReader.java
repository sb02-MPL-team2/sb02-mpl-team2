package com.codeit.sb02mplteam2.domain.recommendation.batch.reader;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistItemRepository;
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
public class RecommendReader implements ItemReader<PlaylistItem> {

  private final PlaylistItemRepository playlistItemRepository;
  private int pageDataIndex = 0;
  private List<PlaylistItem> data;
  private int pageNumber = 0;
  private final int pageSize = 1000; // Adjust page size as needed

  @Override
  public PlaylistItem read() {
    if (data == null || pageDataIndex >= data.size()) {
      Pageable pageable = PageRequest.of(pageNumber, pageSize);
      data = playlistItemRepository.findAll(pageable).getContent();
      pageNumber++;
      pageDataIndex = 0;
      if (data.isEmpty()) {
        return null;
      }
    }
    return data.get(pageDataIndex++);
  }
}
