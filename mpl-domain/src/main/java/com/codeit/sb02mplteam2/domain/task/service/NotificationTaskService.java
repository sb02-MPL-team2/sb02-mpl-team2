package com.codeit.sb02mplteam2.domain.task.service;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;

public interface NotificationTaskService {

  Notification create(Long receiverId, Long publisherId, NotificationType type, Long targetId);

//  <T> List<Notification> createAll(Set<User> receivers, User publisher, NotificationType type, T target);
}
