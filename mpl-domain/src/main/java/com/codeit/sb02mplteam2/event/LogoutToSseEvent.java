package com.codeit.sb02mplteam2.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LogoutToSseEvent extends ApplicationEvent {
  private final Long userId;

  public LogoutToSseEvent(Object source, Long userId) {
    super(source);
    this.userId = userId;
  }
}
