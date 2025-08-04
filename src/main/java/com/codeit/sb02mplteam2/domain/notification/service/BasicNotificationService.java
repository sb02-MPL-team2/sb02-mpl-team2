package com.codeit.sb02mplteam2.domain.notification.service;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.playlist.PlaylistException;
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
  private final AlarmSettingRepository alarmSettingRepository;
  private final PlaylistRepository playlistRepository;

//  @Override
//  public List<NotificationDto> findAllByReceiverId(Long receiverId) {
//    return List.of();
//  }

  @Override
  public void delete(Long notificationId) {
    notificationRepository.deleteById(notificationId);
  }

  @Override
  public NotificationDto create(Long receiverId, NotificationType type, Long targetId, Long publisherId) {
    Notification notification = of(receiverId, publisherId, type, targetId);

    if (notification == null) {
      return null;
    }

    notificationRepository.save(notification);
    return NotificationDto.from(notification);
  }

  @Override
  public List<NotificationDto> createAll(Set<Long> receiverIds, NotificationType type, Long targetId,
      Long publisherId) {
    List<Notification> notificationList = new ArrayList<>();

    for (Long receiverId : receiverIds) {
      Notification notification = of(receiverId, publisherId, type, targetId);
      if (notification != null) {
        notificationList.add(notification);
      }
    }

    notificationRepository.saveAll(notificationList);

    List<NotificationDto> notificationDtoList = new ArrayList<>();
    for (Notification notification : notificationList) {
      notificationDtoList.add(NotificationDto.from(notification));
    }

    return notificationDtoList;
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

    return Notification.of(receiverId, publisherId,title, content ,type, alarmSetting);
  }

  private String createContent(Long targetId, NotificationType type) {
    return switch (type) {
      case NEW_MESSAGE -> //TODO DM Repository 에서 targetId 찾을 예정
          "아직 DM Repository가 없기에 빈 String 파일을 던집니다.";
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED -> {
        Playlist playlist = playlistRepository.findById(targetId)
            .orElseThrow(() -> new PlaylistException(ErrorCode.PLAYLIST_NOT_FOUND));
        yield playlist.getTitle();
      }
      default -> null;
    };
  }
}
