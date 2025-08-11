package com.codeit.sb02mplteam2.domain.recommendation.batch;

import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.entity.PlaylistSubscriberHistory;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistSubscriberHistoryRepository;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class RecommendProcessor implements ItemProcessor<Playlist, PlaylistScore> {

  private final PlaylistSubscriberHistoryRepository historyRepository;

  @Value("#{jobParameters['start']}")
  private LocalDateTime startDate;

  @Value("#{jobParameters['end']}")
  private LocalDateTime endDate;

  @Override
  public PlaylistScore process(Playlist playlist) throws Exception {
    List<PlaylistSubscriberHistory> histories = historyRepository.findByPlaylistAndCreatedAtBetween(
        playlist, startDate, endDate);

    List<Double> counts = new java.util.ArrayList<>(
        histories.stream().map(h -> (double) h.getCount()).toList());

    if (counts.size() < 4) {
      if (counts.isEmpty()) {
        return null;
      }
      double simpleAverage = counts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
      return new PlaylistScore(playlist, simpleAverage);
    }
    //정렬
    Collections.sort(counts);

    //1사분위수, 3사분위수 계산
    double q1 = getPercentile(counts, 25);
    double q3 = getPercentile(counts, 75);

    //IQR 계산
    double iqr = q3 - q1;

    //최대치 계산
    double upperBound = q3 + 1.5 * iqr;

    //이상치 제거
    List<Double> normalData = counts.stream()
        .filter(count -> count <= upperBound)
        .toList();

    if (normalData.isEmpty()) {
      return null;
    }

    //값 정제
    double finalScore = normalData.stream()
        .mapToDouble(Double::doubleValue)
        .average()
        .orElse(0.0);

    return new PlaylistScore(playlist, finalScore);
  }

  /** 퍼센트 위치에 해당하는 인덱스 값 추출하는 로직
   * @param data 정렬된 배열
   * @param percentile 퍼센트
   * @return 배열의 값
   */
  private double getPercentile(List<Double> data, double percentile) {
    int index = (int) Math.ceil(percentile / 100.0 * data.size());
    return data.get(index - 1);
  }
}
