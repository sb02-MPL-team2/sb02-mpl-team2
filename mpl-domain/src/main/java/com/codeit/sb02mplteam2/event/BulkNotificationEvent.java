package com.codeit.sb02mplteam2.event;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.util.Set;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BulkNotificationEvent extends ApplicationEvent {
  private final Set<Long> receiverIds; // 알림 받을 사용자 ID
  private final NotificationType notificationType;
  private final Long targetId; // 알림과 관련된 플레이리스트 ID, DM ID
  private final Long publisherId; //이벤트를 발생시킨 사용자 ID

  public BulkNotificationEvent(Object source, Set<Long> receiverIds,
      NotificationType notificationType, Long targetId, Long publisherId) {
    super(source);
    this.receiverIds = receiverIds;
    this.notificationType = notificationType;
    this.targetId = targetId;
    this.publisherId = publisherId;
  }
}
