package com.codeit.sb02mplteam2.event;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationSendEvent extends ApplicationEvent {

  private final NotificationDto notificationDto;

  public NotificationSendEvent(Object source, NotificationDto notificationDto) {
    super(source);
    this.notificationDto = notificationDto;
  }
}
