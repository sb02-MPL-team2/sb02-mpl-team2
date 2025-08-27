package com.codeit.sb02mplteam2.domain.subscribe;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public void sendEvent(Long receiverId, Long publisherId, Long playlistId) {
    //이벤트 발행
    NotificationEvent event = new NotificationEvent(this, receiverId,
        NotificationType.PLAYLIST_SUBSCRIBED,
        playlistId, publisherId);
    eventPublisher.publishEvent(event);
  }

}
