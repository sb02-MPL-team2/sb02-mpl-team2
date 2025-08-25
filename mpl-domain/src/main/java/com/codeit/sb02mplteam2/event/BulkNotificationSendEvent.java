package com.codeit.sb02mplteam2.event;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import java.util.List;

public record BulkNotificationSendEvent(
    List<Notification> notifications
) {

}
