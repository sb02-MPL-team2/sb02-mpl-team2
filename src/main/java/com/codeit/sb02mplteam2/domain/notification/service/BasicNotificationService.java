package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService{

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

//  @Override
//  public List<NotificationDto> findAllByReceiverId(Long receiverId) {
//    return List.of();
//  }

  @Override
  public void delete(Long notificationId, Long receiverId) {

  }

  @Override
  public NotificationDto create(Long receiverId, NotificationType notificationType, Long targetId,
      Long publisherId) {
    User user = userRepository.findById(receiverId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );
    String title = notificationType.getTitle();
    String content = user.getUsername() + notificationType.getMessageTemplate();

    Notification notification = Notification.builder()
        .receiverId(receiverId)
        .title(title)
        .content(content)
        .type(notificationType)
        .targetId(targetId)
        .publisherId(publisherId)
        .build();
    notificationRepository.save(notification);
    return NotificationDto.from(notification);
  }

  @Override
  public List<NotificationDto> createAll(Set<Long> receiverIds, NotificationType notificationType, Long targetId,
      Long publisherId) {
    String title = notificationType.getTitle();
    List<NotificationDto> notificationDtoList = new ArrayList<>();

    for (Long receiverId : receiverIds) {
      User user = userRepository.findById(receiverId).orElseThrow(
          () -> new UserException(ErrorCode.USER_NOT_FOUND)
      );
      String content = user.getUsername() + notificationType.getMessageTemplate();
      Notification notification = Notification.builder()
          .receiverId(receiverId)
          .title(title)
          .content(content)
          .type(notificationType)
          .targetId(targetId)
          .publisherId(publisherId)
          .build();
      notificationRepository.save(notification);
      notificationDtoList.add(NotificationDto.from(notification));
    }
    return notificationDtoList;
  }
}
