package com.codeit.sb02mplteam2.domain.notification.entity;

import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

  @Id
  @GeneratedValue
  private Long id;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "receiver_id", columnDefinition = "BIGINT", nullable = false)
  private Long receiverId;

  @Column(name = "publisher_id", columnDefinition = "BIGINT")
  private Long publisherId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  //TODO 인수타입이 너무 많음, 줄이는 방법 강구해야함
  public static Notification of(Long receiverId, Long publisherId, String title, String content, NotificationType type, AlarmSetting alarmSetting) {
    if (!isAlarmEnabled(type, alarmSetting)) {
      return null;
    }

    return Notification.builder()
        .receiverId(receiverId)
        .publisherId(publisherId)
        .title(title)
        .content(content != null ? content : "") // content가 null일 경우를 대비해 기본값 처리
        .type(type)
        .build();
  }

  private static boolean isAlarmEnabled(NotificationType type, AlarmSetting alarmSetting) {
    if (type == NotificationType.ASYNC_FAILED) {
      return true;
    }

    return switch (type) {
      case NEW_MESSAGE -> alarmSetting.getDmAlarmEnabled();
      case NEW_PLAYLIST_BY_FOLLOWING -> alarmSetting.getNewPlaylistFromFollowingAlarmEnabled();
      case PLAYLIST_SUBSCRIBED -> alarmSetting.getSubscribePlaylistAlarmEnable();
      case NEW_FOLLOWER -> alarmSetting.getFollowAlarmEnabled();
      case ROLE_CHANGED -> alarmSetting.getPermissionChangeAlarmEnabled();
      default -> false; // 모르는 타입은 false
    };
  }

}
