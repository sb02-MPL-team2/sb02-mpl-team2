package com.codeit.sb02mplteam2.domain.notification.service;

import static com.codeit.sb02mplteam2.util.NotificationUtil.createContent;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicNotificationService implements NotificationService {

  @Value("${mpl.notification.retention-period-in-days}")
  private long notificationRetentionPeriodInDays;

  protected final UserRepository userRepository;
  protected final NotificationRepository notificationRepository;

  @Override
  @Transactional(readOnly = true)
  public List<NotificationDto> findByLastEventTime(Long userId, LocalDateTime lastEventTime) {
    log.info("알람 찾기 서비스 실행중");
    return notificationRepository.findUserNotificationAfter(userId,
            lastEventTime).stream()
        .map(NotificationDto::of).toList();
  }

  @Override
  @Transactional
  public void delete(Long notificationId) {
    log.info("알람 아이디 {}", notificationId);
    if (notificationId == null) {
      log.warn("알람 아이디가 Null");
      return;
    }
    notificationRepository.deleteById(notificationId);
  }

  @Override
  @Transactional
  public void deleteAllByUserId(Long userId) {
    log.info("알람 전체 삭제할 유저 아이디 {}", userId);
    if (userId == null) {
      log.warn("유저 아이디가 null");
      return;
    }
    notificationRepository.deleteAllByReceiverId(userId);
  }

  @Transactional
  @Scheduled(cron = "0 0 0 * * *")
  protected void deleteOldNotification() {
    //현재보다 ?일 이전의 알람 싸그리 삭제함
    LocalDateTime cutoffDateTime = LocalDateTime.now().minusDays(notificationRetentionPeriodInDays);
    log.info("{}일 이전의 오래된 알림 데이터 삭제를 시작합니다. (기준 시각: {})", notificationRetentionPeriodInDays,
        cutoffDateTime);
    notificationRepository.deleteByCreatedAtBefore(cutoffDateTime);
    log.info("오래된 알림 데이터 삭제 완료.");
  }

  /**
   * 모든 값이 캐시에 존재할 경우
   */
  @Override
  @Transactional
  public <T> NotificationDto save(UserDto receiver, UserDto publisher, NotificationType type,
      T target) {
    Notification notification = createNotification(receiver, publisher, type, target);
    Notification saved = notificationRepository.saveAndFlush(notification);
    return NotificationDto.of(saved);
  }

  @Override
  @Transactional
  public <T> List<NotificationDto> saveAll(Set<UserDto> receivers, UserDto publisher,
      NotificationType type, T target) {
    List<Notification> notificationList = new ArrayList<>();
    for (UserDto receiver : receivers) {
      Notification notification = createNotification(receiver, publisher, type, target);
      notificationList.add(notification);
    }
    List<Notification> saved = notificationRepository.saveAllAndFlush(notificationList);
    return saved.stream().map(NotificationDto::of).toList();
  }

  private <T> Notification createNotification(UserDto receiver, UserDto publisher, NotificationType type,
      T target) {
    String title = NotificationType.toTitle(publisher.username(), type.getMessageTemplate());
    String content = createContent(target, type);
    Long targetId = null;
    log.info("저장중");
    if (target instanceof PlaylistDto playlistDto) {
      targetId = playlistDto.id();
    } else if (target instanceof DirectMessageDto directMessageDto) {
      targetId = directMessageDto.id();
    }
    log.info("targetId {}", targetId);
    return Notification.of(receiver.id(), publisher.id(), targetId,
        title, content, type);
  }
}
