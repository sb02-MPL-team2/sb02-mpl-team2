package com.codeit.sb02mplteam2.domain.recommendation.batch.processor;

import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistItem;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class RecommendProcessor implements ItemProcessor<PlaylistItem, PlaylistScore> {
  //TODO 플리 점수, 리뷰수 점수, 평점 점수 로직을 각각 서비스에 할당해서 처리

  @Override
  public PlaylistScore process(PlaylistItem item) throws Exception {
    return null;
  }
}
