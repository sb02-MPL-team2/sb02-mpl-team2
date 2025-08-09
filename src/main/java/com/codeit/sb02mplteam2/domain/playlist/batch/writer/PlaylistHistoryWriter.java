package com.codeit.sb02mplteam2.domain.playlist.batch.writer;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistSubscriberHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class PlaylistHistoryWriter implements ItemWriter<PlaylistSubscriberHistory> {
  private final PlaylistSubscriberHistoryRepository playlistSubscriberHistoryRepository;

  @Override
  public void write(Chunk<? extends PlaylistSubscriberHistory> chunk) throws Exception {
    List<? extends PlaylistSubscriberHistory> items = chunk.getItems();

    playlistSubscriberHistoryRepository.saveAll(items);
  }
}
