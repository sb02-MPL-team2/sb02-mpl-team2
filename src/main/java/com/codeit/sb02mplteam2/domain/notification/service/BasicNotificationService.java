package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService{

  private final NotificationRepository notificationRepository;

  @Override
  public List<NotificationDto> findAllByReceiverId(Long receiverId) {
    return List.of();
  }

  @Override
  public void delete(Long notificationId, Long receiverId) {

  }

  @Override
  public void create(Long receiverId, NotificationType notificationType, Long targetId,
      Long publisherId) {
    
  }

  @Override
  public void createAll(Set<Long> receiverIds, NotificationType notificationType, Long targetId,
      Long publisherId) {

  }
}
