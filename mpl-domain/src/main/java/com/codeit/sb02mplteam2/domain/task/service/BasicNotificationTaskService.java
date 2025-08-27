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
import com.codeit.sb02mplteam2.domain.user.service.UserQueryService;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationTaskService implements NotificationTaskService {

  private final NotificationRepository notificationRepository;
  private final UserQueryService userQueryService;
  private final DirectMessageQueryService directMessageQueryService;
  private final PlaylistService playlistService;

  @Override
  public Notification create(Long receiverId, Long publisherId, NotificationType type,
      Long targetId) {
    UserDto receiver = userQueryService.findByUserId(receiverId);
    UserDto publisher = userQueryService.findByUserId(publisherId);
    Notification notification = of(receiver, publisher, type, targetId);
    notificationRepository.save(notification);
    return notification;
  }

  @Override
  public Notification create(NotificationEvent originalEvent) {
    Long receiverId = originalEvent.getReceiverId();
    Long publisherId = originalEvent.getPublisherId();
    NotificationType type = originalEvent.getNotificationType();
    Long targetId = originalEvent.getTargetId();
    return create(receiverId, publisherId, type, targetId);
  }

  @Override
  public List<Notification> create(BulkNotificationEvent bulkEvent) {
    log.info("대량의 알람을 레포지토리에서 검색해서 생성합니다.");
    Set<Long> receiverIds = bulkEvent.getReceiverIds();
    Long publisherId = bulkEvent.getPublisherId();
    NotificationType type = bulkEvent.getNotificationType();
    Long targetId = bulkEvent.getTargetId();
    return createAll(receiverIds, publisherId, type, targetId);
  }

  @Override
  public List<Notification> createAll(Set<Long> receiverIds, Long publisherId,
      NotificationType type, Long target) {
    List<Notification> notifications = new ArrayList<>();
    for (Long receiverId : receiverIds) {
      Notification notification = create(receiverId, publisherId, type, target);
      notifications.add(notification);
    }
    notificationRepository.saveAll(notifications);
    log.info("대량의 알람 샏성 성공");
    return notifications;
  }

  private Notification of(UserDto receiver, UserDto publisher, NotificationType type,
      Long targetId) {
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
