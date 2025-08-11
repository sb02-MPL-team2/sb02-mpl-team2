package com.codeit.sb02mplteam2.domain.playlist.batch;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class PlaylistProcessor implements ItemProcessor<Playlist, PlaylistSubscriberHistory> {

  @Override
  public PlaylistSubscriberHistory process(Playlist item) throws Exception {
    int size = item.getSubscribes().size();

    return new PlaylistSubscriberHistory(item, size);
  }
}
