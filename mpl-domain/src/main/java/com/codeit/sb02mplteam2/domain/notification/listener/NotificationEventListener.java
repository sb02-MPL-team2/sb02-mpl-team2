package com.codeit.sb02mplteam2.domain.notification.listener;

import static com.codeit.sb02mplteam2.util.CommonUtil.retrieveAllFromCache;
import static com.codeit.sb02mplteam2.util.CommonUtil.retrieveCache;
import static com.codeit.sb02mplteam2.util.NotificationUtil.typeFiltering;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.event.LostNotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.event.NotificationEvent;
import com.codeit.sb02mplteam2.domain.notification.service.DeliveryService;
import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.playlist.entity.Playlist;
import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.setting.service.AlarmSettingService;
import com.codeit.sb02mplteam2.domain.social.entity.DirectMessage;
import com.codeit.sb02mplteam2.domain.task.service.NotificationTaskService;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener implements NotificationListener {

  private final NotificationTaskService notificationTaskService;
  private final AlarmSettingService alarmSettingService;
  private final NotificationService notificationService;
  private final DeliveryService deliveryService;
  private final CacheManager cacheManager;

  private Cache alarmCache;
  private Cache userCache;
  private Cache playlistCache;
  private Cache messageCache;

  @PostConstruct
  public void validateCaches() {
    log.info("NotificationRabbitMQListener 초기화 시작");
    this.alarmCache = cacheManager.getCache("alarms");
    if (alarmCache == null) {
      throw new IllegalStateException("필수 캐시 'alarms'가 존재하지 않습니다.");
    }

    this.userCache = cacheManager.getCache("users");
    if (userCache == null) {
      // 애플리케이션을 시작하지 못하도록 예외를 발생시킵니다.
      throw new IllegalStateException("필수 캐시 'users'가 존재하지 않습니다.");
    }

    this.playlistCache = cacheManager.getCache("playlists");
    if (playlistCache == null) {
      throw new IllegalStateException("필수 캐시 'playlists'가 존재하지 않습니다.");
    }

    this.messageCache = cacheManager.getCache("messages");
    if (messageCache == null) {
      throw new IllegalStateException("필수 캐시 'messages'가 존재하지 않습니다.");
    }

    log.info("모든 필수 캐시(users, playlists, messages)가 성공적으로 로드되었습니다.");
  }

  // All or Nothing
  @Override
  @RabbitListener(queues = "notification.queue")
  public void handleNotificationEvent(NotificationEvent event) {

    Long receiverId = event.getReceiverId();
    Long publisherId = event.getPublisherId();
    Long targetId = event.getTargetId();
    NotificationType type = event.getNotificationType();
    log.info("실시간 알람 큐에 메시지를 받았습니다. Type: {}", type);

    //alarmSetting 확인
    AlarmSetting alarmSetting = retrieveCache(alarmCache, receiverId, AlarmSetting.class);
    if (alarmSetting == null) {
      alarmSetting = alarmSettingService.findByUserId(receiverId);
    }

    boolean enabled = typeFiltering(type, alarmSetting);
    if (!enabled) {
      log.info("사용자(ID: {})가 해당 알림({}) 수신을 설정하지 않았습니다.", event.getReceiverId(),
          event.getNotificationType());
      return;
    }

    //receiver와 publisher 캐시값 확인
    UserDto receiver = retrieveCache(userCache, receiverId, UserDto.class);
    UserDto publisher = retrieveCache(userCache, publisherId, UserDto.class);
    Object targetEntity = null;

    //Type에 따라 조회
    switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST:
        targetEntity = retrieveCache(playlistCache, targetId, PlaylistDto.class);
        break;
      case NEW_MESSAGE:
        targetEntity = retrieveCache(messageCache, targetId, DirectMessage.class);
        break;
      default:
        break;
    }

    //캐시 데이터 유효성 검증
    boolean isCacheMiss = (receiver == null || publisher == null);

    // Target 엔티티가 필요한 타입인데 캐시에 없는 경우 Cache Miss로 처리
    if (!isCacheMiss && isTargetRequired(type) && targetEntity == null) {
      isCacheMiss = true;
    }

    if (isCacheMiss) {
      // 하나라도 캐시에 없으면 Task Service에 작업을 위임하여 DB에서 조회 및 처리
      log.warn("캐시 미스 발생. Notification 생성을 Task Service로 위임합니다. Event: {}", event);
      delegateToTaskService(event); // Task Service로 이벤트를 다시 보내는 로직
    } else {
      // 모든 데이터가 캐시에 존재하므로 즉시 SSE 등으로 알림 전송
      log.info("모든 데이터가 캐시에 존재합니다. 실시간 알림을 처리합니다.");
      processNotificationWithCachedData(receiver, publisher, targetEntity, type);
    }
  }

  /**
   * 대규모 알림 이벤트를 벌크 조회와 작업 분리 전략으로 처리
   */
  @Override
  public void handleBulkNotificationEvent(BulkNotificationEvent event) {
    NotificationType type = event.getNotificationType();
    log.info("실시간 대규모 큐 메시지 수신. Type: {}", type);

    // 공통 데이터(Publisher, Target) 우선 조회
    UserDto publisher = retrieveCache(userCache, event.getPublisherId(), UserDto.class);

    Object targetEntity = switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST:
        yield retrieveCache(playlistCache, event.getTargetId(), Playlist.class);
      default:
        yield null;
    };

    // 공통 데이터 중 하나라도 캐시에 없으면, 전체 이벤트를 Task Service로 위임
    if (publisher == null || (isTargetRequired(type) && targetEntity == null)) {
      log.warn("공통 데이터(Publisher 또는 Target) 캐시 미스. 전체 Bulk 이벤트를 Task Service로 위임합니다.");
      delegateBulkTaskToService(event); // 전체 이벤트를 그대로 위임
      return;
    }

    // 모든 수신자 정보를 '벌크'로 캐시에서 조회
    Set<Long> receiverIds = event.getReceiverIds();
    // 가정: retrieveAllFromCache는 Map<ID, 객체>를 반환. 조회 실패 시 해당 ID는 Map에 포함되지 않음.
    Map<Long, UserDto> cachedUsers = retrieveAllFromCache(userCache, receiverIds, UserDto.class);
    Map<Long, AlarmSetting> cachedAlarmSettings = retrieveAllFromCache(alarmCache, receiverIds, AlarmSetting.class);

    // Fast Path와 Slow Path로 작업 분리
    Set<UserDto> fastPathReceivers = new HashSet<>();
    Set<Long> slowPathReceiverIds = new HashSet<>();

    for (Long receiverId : receiverIds) {
      UserDto receiver = cachedUsers.get(receiverId);
      AlarmSetting setting = cachedAlarmSettings.get(receiverId);

      // User와 AlarmSetting이 모두 캐시에 존재하는 경우
      if (receiver != null && setting != null) {
        if (typeFiltering(type, setting)) {
          fastPathReceivers.add(receiver); // 알림 설정 ON -> Fast Path
        }
        // 알림 설정 OFF -> 아무것도 안 함
      } else {
        // User 또는 AlarmSetting 둘 중 하나라도 캐시에 없는 경우
        slowPathReceiverIds.add(receiverId); // -> Slow Path
      }
    }

    // 즉시 처리 가능한 대상이 있다면 알림 전송
    if (!fastPathReceivers.isEmpty()) {
      log.info("[Fast Path] {}명의 사용자에게 즉시 알림을 보냅니다.", fastPathReceivers.size());
      processBulkNotificationsWithCachedData(fastPathReceivers, publisher, targetEntity, type);
    }

    // DB 조회가 필요한 대상이 있다면 Task Service로 위임
    if (!slowPathReceiverIds.isEmpty()) {
      log.warn("[Slow Path] {}명의 사용자에 대한 처리를 Task Service로 위임합니다.", slowPathReceiverIds.size());
      BulkNotificationEvent bulkEvent = new BulkNotificationEvent(this,
          slowPathReceiverIds, event.getNotificationType(), event.getTargetId(), event.getPublisherId()
      );
      delegateBulkTaskToService(bulkEvent);
    }
  }

  @Override
  public void handleLostNotificationEvent(LostNotificationEvent event) {
    Long receiverId = event.getUserId();
    LocalDateTime lastEventTime = event.getLastEventTime();
    log.info("마지막 알람 전송 이후 미전송된 알람 전송 : 유저 아이디 {}, 마지막 전송 시각 {}", receiverId, lastEventTime);
    List<NotificationDto> lostNotificationList = notificationService.findByLastEventTime(receiverId,
        event.getLastEventTime());

    if (lostNotificationList.isEmpty()) {
      log.warn("미전송된 알람이 존재하지 않습니다.");
      return;
    }

    for (NotificationDto notificationDto : lostNotificationList) {
      deliveryService.deliverToClient(notificationDto);
    }
  }

  private boolean isTargetRequired(NotificationType type) {
    return switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST, NEW_MESSAGE ->
          true;
      default -> false;
    };
  }

  /**
   * 캐시된 데이터로 실제 알림을 처리하고 전송
   */
  private void processNotificationWithCachedData(UserDto receiver, UserDto publisher,
      Object targetEntity, NotificationType type) {
    // 직접 SSE 서비스를 호출하는 로직을 구현합니다.
    log.info("[Fast Path] receiver: {}, publisher: {}, target: {}, type: {}", receiver.id(),
        publisher.id(), targetEntity, type);
    NotificationDto notificationDto = notificationService.save(receiver, publisher, type, targetEntity);
    deliveryService.deliverToClient(notificationDto);
  }

  /**
   * Task Service가 이벤트를 처리하도록 메시지를 전달
   */
  private void delegateToTaskService(NotificationEvent originalEvent) {
    // DB 조회가 필요한 작업을 처리하는 별도의 큐로 메시지를 보냅니다.
    log.info("[Slow Path] Event를 notification.task.queue로 전달합니다: {}", originalEvent);
    // TODO 일단 RabbitMQ를 사용하지 않고 작업 서버 거쳐서 던지는 로직 구현함
    Notification notification = notificationTaskService.create(originalEvent);
    NotificationDto notificationDto = NotificationDto.of(notification);
    deliveryService.deliverToClient(notificationDto);
  }
  /**
   * 캐시된 데이터로 대규모 알림을 처리하고 전송하는 메서드
   */
  private void processBulkNotificationsWithCachedData(Set<UserDto> receivers, UserDto publisher, Object targetEntity, NotificationType type) {
    // receivers Set을 순회하며 각 사용자에게 보낼 NotificationDto를 생성하고,
    log.info("{}명에게 알림 DTO 생성 및 전송 로직 실행", receivers.size());
    List<NotificationDto> notificationDtoList = notificationService.saveAll(receivers, publisher,
        type, targetEntity);
    notificationDtoList.forEach(deliveryService::deliverToClient);
  }

  /**
   * Task Service가 Bulk 이벤트를 처리하도록 메시지를 전달하는 메서드
   */
  private void delegateBulkTaskToService(BulkNotificationEvent bulkEvent) {
    // DB 조회가 필요한 작업을 처리하는 별도의 벌크 작업 큐로 메시지를 보냅니다.
    log.info("Bulk Event를 notification.bulk-task.queue로 전달합니다: {}명의 수신자", bulkEvent.getReceiverIds().size());
    // TODO 일단 RabbitMQ를 사용하지 않고 작업 서버 거쳐서 던지는 로직 구현함
    List<Notification> notificationList = notificationTaskService.create(bulkEvent);
    notificationList.forEach(
        notification -> deliveryService.deliverToClient(NotificationDto.of(notification)));
  }
}