package com.codeit.sb02mplteam2.domain.notification.event;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BroadcastEvent extends ApplicationEvent {
  private final LocalDateTime createdAt;
  private final NotificationType notificationType;
  private final Long targetId; // 알림과 관련된 플레이리스트 ID, DM ID

  public BroadcastEvent(Object source, NotificationType notificationType, Long targetId) {
    super(source);
    this.notificationType = notificationType;
    this.targetId = targetId;
    this.createdAt = LocalDateTime.now();
  }

}
