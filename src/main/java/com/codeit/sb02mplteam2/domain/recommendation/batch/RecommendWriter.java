package com.codeit.sb02mplteam2.domain.recommendation.batch;

import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import com.codeit.sb02mplteam2.domain.recommendation.repository.PlaylistScoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class RecommendWriter implements ItemWriter<PlaylistScore> {
  private final PlaylistScoreRepository playlistScoreRepository;

  @Override
  public void write(Chunk<? extends PlaylistScore> chunk) throws Exception {
    List<? extends PlaylistScore> items = chunk.getItems();

    playlistScoreRepository.saveAll(items);

    //TODO 브로드캐스트 로직 실행해야함
  }
}
