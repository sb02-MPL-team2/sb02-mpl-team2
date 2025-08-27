package com.codeit.sb02mplteam2.event;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LostNotificationEvent extends ApplicationEvent {

  private final Long userId;
  private final LocalDateTime lastEventTime;
  private final ConnectionInfo connectionInfo;

  public LostNotificationEvent(Object source, Long userId, LocalDateTime lastEventTime,
      ConnectionInfo connectionInfo) {
    super(source);
    this.userId = userId;
    this.lastEventTime = lastEventTime;
    this.connectionInfo = connectionInfo;
  }
}
