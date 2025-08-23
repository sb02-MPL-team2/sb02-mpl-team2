package com.codeit.sb02mplteam2.domain.recommendation.batch;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.recommendation.entity.PlaylistScore;
import com.codeit.sb02mplteam2.domain.recommendation.repository.PlaylistScoreRepository;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.util.RabbitConst;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class RecommendTasklet implements Tasklet {

  private final UserRepository userRepository;
  private final PlaylistScoreRepository playlistScoreRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final RabbitTemplate rabbitTemplate;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    log.info("상위 추천 플레이리스트 브로드캐스트 시작");

    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

    List<PlaylistScore> topPlaylists = playlistScoreRepository.findTop3ByCreatedAtBetweenOrderByScoreDesc(
        startOfDay, endOfDay);

    if (topPlaylists.isEmpty()) {
      log.warn("오늘 브로드캐스트할 추천 플레이리스트가 없습니다.");
      return RepeatStatus.FINISHED;
    }

    topPlaylists.forEach(playlistScore -> {
      Long playlistId = playlistScore.getPlaylist().getId();
      log.info(" 브로드캐스트 이벤트 발행: Playlist ID = {}, Score = {}", playlistId, playlistScore.getScore());
      Set<Long> userIds = userRepository.findAllIds();
      BulkNotificationEvent event = new BulkNotificationEvent(userIds,
          NotificationType.BROADCAST_TODAY_PLAYLIST, playlistId,
          1L);
      rabbitTemplate.convertAndSend(RabbitConst.notificationExchange, RabbitConst.Notification_Bulk_Send_RoutingKey, event);
    });

    return RepeatStatus.FINISHED;
  }
}
