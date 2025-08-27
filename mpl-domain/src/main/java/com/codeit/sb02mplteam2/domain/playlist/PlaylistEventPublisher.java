package com.codeit.sb02mplteam2.domain.playlist;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaylistEventPublisher {
  private final FollowRepository followRepository;
  private final ApplicationEventPublisher eventPublisher;

  public void sendEvent(Long userId, Long playlistId) {
    //이벤트 발행
    Set<Long> followersId = followRepository.findAllFollowersIdByToUserId(userId);
    log.info("{}의 팔로워에게 알람 전송 ", followersId.size());
    BulkNotificationEvent event = new BulkNotificationEvent(this,
        followersId,
        NotificationType.NEW_PLAYLIST_BY_FOLLOWING,
        playlistId,
        userId);
    eventPublisher.publishEvent(event);
  }
}
