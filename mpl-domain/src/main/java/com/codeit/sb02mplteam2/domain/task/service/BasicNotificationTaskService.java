package com.codeit.sb02mplteam2.domain.task.service;

import static com.codeit.sb02mplteam2.util.NotificationUtil.createContent;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification.NotificationBuilder;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.service.PlaylistService;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageDto;
import com.codeit.sb02mplteam2.domain.social.service.DirectMessageQueryService;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationTaskService implements NotificationTaskService{

  private final NotificationRepository notificationRepository;
  private final UserService userService;
  private final PlaylistService playlistService;
  private final DirectMessageQueryService directMessageQueryService;

  @Override
  public Notification create(Long receiverId, Long publisherId, NotificationType type,
      Long targetId) {

    UserDto receiver = userService.findById(receiverId);
    UserDto publisher = userService.findById(publisherId);
    Notification notification = of(receiver, publisher, type, targetId);

    notificationRepository.save(notification);
    return notification;
  }

//  @Override
//  public <T> List<Notification> createAll(Set<User> receivers, User publisher,
//      NotificationType type, T target) {
//    List<Notification> notifications = new ArrayList<>();
//    for (User receiver : receivers) {
//      Notification notification = of(receiver, publisher, type, target);
//      notifications.add(notification);
//    }
//    notificationRepository.saveAll(notifications);
//
//    return notifications;
//  }

  private Notification of(UserDto receiver, UserDto publisher, NotificationType type, Long targetId) {
    Notification notification = null;
    NotificationBuilder builder = Notification.builder().receiverId(receiver.id())
        .publisherId(publisher.id())
        .type(type)
        .title(NotificationType.toTitle(publisher.username(), type.getMessageTemplate()));
    if (type == NotificationType.NEW_PLAYLIST_BY_FOLLOWING
        || type == NotificationType.PLAYLIST_SUBSCRIBED
        || type == NotificationType.BROADCAST_TODAY_PLAYLIST) {
      PlaylistDto playlistDto = playlistService.findById(targetId);
      return builder.content(createContent(playlistDto, type)).build();
    } else if (type == NotificationType.NEW_MESSAGE) {
      DirectMessageDto directMessageDto = directMessageQueryService.findByDirectMessageId(
          targetId);
      return builder.content(createContent(directMessageDto, type)).build();
    } else {
      notification = builder.content(" ").build();
    }
    return notification;
  }
}
