package com.codeit.sb02mplteam2.domain.notification.listener;

import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.LostNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;

public interface NotificationListener {

  void handleNotificationEvent(NotificationEvent event);

  void handleBulkNotificationEvent(BulkNotificationEvent event);

  void handleLostNotificationEvent(LostNotificationEvent event);

}
