package com.codeit.sb02mplteam2.domain.notification.listener;

import static com.codeit.sb02mplteam2.util.CommonUtil.isTargetRequired;
import static com.codeit.sb02mplteam2.util.CommonUtil.retrieveAllFromCache;
import static com.codeit.sb02mplteam2.util.CommonUtil.retrieveCache;
import static com.codeit.sb02mplteam2.util.NotificationUtil.typeFiltering;

import com.codeit.sb02mplteam2.domain.notification.NotificationEventPublisher;
import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.notification.service.DeliveryService;
import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.setting.service.AlarmSettingService;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.event.BulkNotificationEvent;
import com.codeit.sb02mplteam2.event.BulkNotificationSendEvent;
import com.codeit.sb02mplteam2.event.LostNotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationEvent;
import com.codeit.sb02mplteam2.event.NotificationSendEvent;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener implements NotificationListener {

  private final AlarmSettingService alarmSettingService;
  private final NotificationService notificationService;
  private final DeliveryService deliveryService;
  private final CacheManager cacheManager;
  private final NotificationEventPublisher notificationEventPublisher;

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

    this.messageCache = cacheManager.getCache("directMessages");
    if (messageCache == null) {
      throw new IllegalStateException("필수 캐시 'directMessages'가 존재하지 않습니다.");
    }

    log.info("모든 필수 캐시(users, playlists, directMessages)가 성공적으로 로드되었습니다.");
  }

  // All or Nothing
  @Override
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

    //receiver publisher 캐시값 확인
    UserDto receiver = retrieveCache(userCache, receiverId, UserDto.class);
    UserDto publisher = retrieveCache(userCache, publisherId, UserDto.class);
    //캐시 데이터 유효성 검증
    boolean isCacheMiss = (receiver == null || publisher == null);

    switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST:
        PlaylistDto playlistDto = retrieveCache(playlistCache, targetId, PlaylistDto.class);
        if (!isCacheMiss && playlistDto != null) {
          log.info("플레이리스트가 캐시에 존재합니다. 실시간 알림을 처리합니다.");
          notificationEventPublisher.processNotificationWithCachedData(receiver, publisher,
              playlistDto, type);
          return;
        }
        break;
      case NEW_MESSAGE:
        DirectMessageDto directMessageDto = retrieveCache(messageCache, targetId,
            DirectMessageDto.class);
        if (!isCacheMiss && directMessageDto != null) {
          log.info("DM 메시지가 캐시에 존재합니다. 실시간 알림을 처리합니다.");
          notificationEventPublisher.processNotificationWithCachedData(receiver, publisher,
              directMessageDto, type);
          return;
        }
        break;
      default:
        break;
    }

    // Target 엔티티가 필요한 타입인데 캐시에 없는 경우 Cache Miss로 처리
    if (isCacheMiss) {
      // 하나라도 캐시에 없으면 Task Service에 작업을 위임하여 DB에서 조회 및 처리
      log.warn("캐시 미스 발생. Notification 생성을 Task Service로 위임합니다. Event: {}", event);
      notificationEventPublisher.delegateToTaskService(event); // Task Service로 이벤트를 다시 보내는 로직
    } else {
      log.info("Receiver, Publisher 의 캐시가 존재합니다. 실시간 알림을 처리합니다.");
      notificationEventPublisher.processNotificationWithCachedData(receiver, publisher, null, type);
    }
  }

  /**
   * 대규모 알림 이벤트를 벌크 조회와 작업 분리 전략으로 처리
   */
  @Override
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleBulkNotificationEvent(BulkNotificationEvent event) {
    NotificationType type = event.getNotificationType();
    log.info("실시간 대규모 큐 메시지 수신. Type: {}", type);

    // 공통 데이터(Publisher, Target) 우선 조회
    UserDto publisher = retrieveCache(userCache, event.getPublisherId(), UserDto.class);

    PlaylistDto targetEntity = switch (type) {
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST:
        yield retrieveCache(playlistCache, event.getTargetId(), PlaylistDto.class);
      default:
        yield null;
    };

    // 공통 데이터 중 하나라도 캐시에 없으면, 전체 이벤트를 Task Service로 위임
    if (publisher == null || (isTargetRequired(type) && targetEntity == null)) {
      log.info(
          "공통 데이터(Publisher 또는 Target) 캐시 미스. 전체 Bulk 이벤트를 Task Service로 위임합니다. publisher : {}, targetEntity : {}",
          publisher, targetEntity);
      notificationEventPublisher.delegateBulkTaskToService(event); // 전체 이벤트를 그대로 위임
      return;
    }

    // 모든 수신자 정보를 '벌크'로 캐시에서 조회
    Set<Long> receiverIds = event.getReceiverIds();
    // 가정: retrieveAllFromCache는 Map<ID, 객체>를 반환. 조회 실패 시 해당 ID는 Map에 포함되지 않음.
    Map<Long, UserDto> cachedUsers = retrieveAllFromCache(userCache, receiverIds, UserDto.class);
    Map<Long, AlarmSetting> cachedAlarmSettings = retrieveAllFromCache(alarmCache, receiverIds,
        AlarmSetting.class);

    // Fast Path와 Slow Path로 작업 분리
    Set<UserDto> fastPathReceivers = new HashSet<>();
    Set<Long> slowPathReceiverIds = new HashSet<>();

    for (Long receiverId : receiverIds) {
      UserDto receiver = cachedUsers.get(receiverId);
      AlarmSetting setting = cachedAlarmSettings.get(receiverId);
      if (setting == null) {
        setting = alarmSettingService.findByUserId(receiverId);
      }
      if (typeFiltering(type, setting)) {
        if (receiver != null) {
          fastPathReceivers.add(receiver); // 알림 설정 ON -> Fast Path
        } else {
          slowPathReceiverIds.add(receiverId); // -> Slow Path
        }
      }
    }

    // 즉시 처리 가능한 대상이 있다면 알림 전송
    if (!fastPathReceivers.isEmpty()) {
      log.info("[Fast Path] {}명의 사용자에게 즉시 알림을 보냅니다.", fastPathReceivers.size());
      notificationEventPublisher.processBulkNotificationsWithCachedData(fastPathReceivers,
          publisher, targetEntity, type);
    }

    // DB 조회가 필요한 대상이 있다면 Task Service로 위임
    if (!slowPathReceiverIds.isEmpty()) {
      log.warn("[Slow Path] {}명의 사용자에 대한 처리를 Task Service로 위임합니다.", slowPathReceiverIds.size());
      BulkNotificationEvent bulkEvent = new BulkNotificationEvent(this, slowPathReceiverIds,
          event.getNotificationType(), event.getTargetId(), event.getPublisherId()
      );
      notificationEventPublisher.delegateBulkTaskToService(bulkEvent);
    }
  }

  @Override
  @EventListener
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

  @EventListener
  public void handleNotificationReceiveEvent(NotificationSendEvent event) {
    log.info("성공적으로 task 작업에서 수행한 알람을 수신 받았습니다. id: {}", event.getNotificationDto().id());
    deliveryService.deliverToClient(event.getNotificationDto());
  }

  @EventListener
  public void handleNotificationReceiveBulkEvent(BulkNotificationSendEvent event) {
    log.info("성공적으로 task 작업에서 수행한 대량 알람 수신을 받았습니다. size: {}", event.notifications().size());
    event.notifications()
        .forEach(notification -> deliveryService.deliverToClient(NotificationDto.of(notification)));
  }
}