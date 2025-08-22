package com.codeit.sb02mplteam2.util;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.social.dto.DirectMessageDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // private 생성자로 인스턴스화 방지
public final class NotificationUtil {
  public static boolean typeFiltering(NotificationType type, AlarmSetting alarmSetting) {
    return switch (type) {
      case NEW_MESSAGE -> alarmSetting.getDmAlarmEnabled();
      case NEW_FOLLOWER -> alarmSetting.getFollowAlarmEnabled();
      case NEW_PLAYLIST_BY_FOLLOWING -> alarmSetting.getNewPlaylistFromFollowingAlarmEnabled();
      case PLAYLIST_SUBSCRIBED -> alarmSetting.getSubscribePlaylistAlarmEnable();
      case ROLE_CHANGED -> alarmSetting.getPermissionChangeAlarmEnabled();
      case BROADCAST_TODAY_PLAYLIST -> alarmSetting.getRecommendPlaylistAlarmEnabled();
      default -> false;
    };
  }

  public static <T> String createContent(T target, NotificationType type) {
    return switch (type) {
      case NEW_MESSAGE -> {
        if (target instanceof DirectMessageDto directMessageDto) {
          yield directMessageDto.content();
        } else {
          throw new IllegalArgumentException("잘못된 요소가 들어왔습니다.");
        }
      }
      case NEW_PLAYLIST_BY_FOLLOWING, PLAYLIST_SUBSCRIBED, BROADCAST_TODAY_PLAYLIST -> {
        if (target instanceof PlaylistDto playlistDto) {
          yield playlistDto.title();
        } else {
          throw new IllegalArgumentException("잘못된 요소가 들어왔습니다.");
        }
      }
      default -> null;
    };
  }
}
