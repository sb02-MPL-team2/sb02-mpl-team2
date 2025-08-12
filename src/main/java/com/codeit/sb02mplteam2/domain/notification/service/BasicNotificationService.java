package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.event.BroadcastEvent;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.social.repository.DirectMessageRepository;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.directmessage.DirectMessageException;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

  @Value("${mpl.notification.retention-period-in-days}")
  private long notificationRetentionPeriodInDays;

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final DirectMessageRepository directMessageRepository;
  private final AlarmSettingRepository alarmSettingRepository;
  private final PlaylistRepository playlistRepository;

  @Override
  public List<NotificationDto> findByLastEventTime(Long userId, LocalDateTime lastEventTime) {
    log.info("알람 찾기 서비스 실행중");
    return notificationRepository.findUserNotificationAfter(userId,
            lastEventTime).stream()
        .map(NotificationDto::of).toList();
  }

  @Override
  public void delete(Long notificationId) {
    if (notificationId == null) {
      return;
    }
    notificationRepository.deleteById(notificationId);
  }

  @Override
  public void deleteAllByUserId(Long userId) {
    if (userId == null) {
      return;
    }
    notificationRepository.deleteAllByReceiverId(userId);
  }

  @Scheduled(cron = "0 0 0 * * *")
  private void deleteOldNotification() {
    //현재보다 ?일 이전의 알람 싸그리 삭제함
    LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(notificationRetentionPeriodInDays);
    log.info("{}일 이전의 오래된 알림 데이터 삭제를 시작합니다. (기준 시각: {})", notificationRetentionPeriodInDays,
        cutoffDateTime);
    notificationRepository.deleteByCreatedAtBefore(cutoffDateTime);
    log.info("오래된 알림 데이터 삭제 완료.");
  }

  @Override
  public NotificationDto broadcast(BroadcastEvent event) {
    NotificationType notificationType = event.getNotificationType();
    Long targetId = event.getTargetId();
    LocalDateTime twelveHoursAgo = LocalDateTime.now().minusHours(12);

    Optional<Notification> broadcastNotification = notificationRepository.findByTypeAndTargetIdAndCreatedAtAfter(
        notificationType, targetId, twelveHoursAgo);

    if (broadcastNotification.isPresent()) {
      return NotificationDto.of(broadcastNotification.get());
    } else {
      log.info("브로드캐스트 알람 생성");
      String title = notificationType.getTitle();
      String content = createContent(targetId, notificationType);

      Notification notification = Notification.broadcast(targetId, title, content, notificationType);
      notificationRepository.save(notification);

      return NotificationDto.of(notification);
    }
  }

  @Override
  public NotificationDto create(Long receiverId, NotificationType type, Long targetId,
      Long publisherId) {
    Notification notification = of(receiverId, publisherId, type, targetId);

    if (notification == null) {
      return null;
    }

    notificationRepository.save(notification);
    return NotificationDto.of(notification);
  }

  @Override
  public List<NotificationDto> createAll(Set<Long> receiverIds, NotificationType type,
      Long targetId,
      Long publisherId) {

    User publisher = userRepository.findById(publisherId)
        .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

    List<User> receivers = userRepository.findAllById(receiverIds);

    // Map<userId, AlarmSetting> 형태로 변환하여 O(1) 시간 복잡도로 조회를 가능하게 함
    Map<Long, AlarmSetting> alarmSettingsMap = alarmSettingRepository.findAllByUserIn(receivers)
        .stream()
        .collect(Collectors.toMap(setting -> setting.getUser().getId(), setting -> setting));

    List<Notification> notificationsToSave = new ArrayList<>();
    String titleTemplate = type.getMessageTemplate();
    String content = createContent(targetId, type);

    for (User receiver : receivers) {
      AlarmSetting alarmSetting = alarmSettingsMap.get(receiver.getId());

      if (alarmSetting == null) {
        continue;
      }

      String title = NotificationType.toTitle(publisher.getUsername(), titleTemplate);

      Notification notification = Notification.of(receiver.getId(), publisherId, targetId,title, content,
          type, alarmSetting);
      if (notification != null) {
        notificationsToSave.add(notification);
      }
    }

    List<Notification> savedNotifications = notificationRepository.saveAll(notificationsToSave);

    return savedNotifications.stream()
        .map(NotificationDto::of)
        .toList();
  }

  private Notification of(Long receiverId, Long publisherId, NotificationType type, Long targetId) {
    User receiver = userRepository.findById(receiverId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    //TODO 유저에 알람 설정 파일을 가져올 수 있으면 좋을듯
    AlarmSetting alarmSetting = alarmSettingRepository.findByUser(receiver).orElseThrow(
        () -> new MplException(ErrorCode.USER_NOT_FOUND)
    );

    User publisher = userRepository.findById(publisherId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND)
    );

    String title = NotificationType.toTitle(publisher.getUsername(), type.getMessageTemplate());
    String content = createContent(targetId, type);

    return Notification.of(receiverId, publisherId, targetId, title, content, type, alarmSetting);
  }

  private String createContent(Long targetId, NotificationType type) {
    return switch (type) {
      case NEW_MESSAGE -> {
        DirectMessage directMessage = directMessageRepository.findById(targetId)
            //TODO ErrorCode에 DM Not found 넣어야함
            .orElseThrow(() -> new DirectMessageException(ErrorCode.INTERNAL_SERVER_ERROR));
        yield directMessage.getContent();
      }
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST -> {
        Playlist playlist = playlistRepository.findById(targetId)
            .orElseThrow(() -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
        yield playlist.getTitle();
      }
      default -> null;
    };
  }
}
