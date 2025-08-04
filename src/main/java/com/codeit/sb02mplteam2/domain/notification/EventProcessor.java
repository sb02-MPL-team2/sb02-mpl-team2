package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.dto.NotificationDto;
import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProcessor {

  private final ConnectionManager connectionManager;
  private final AlarmSettingRepository alarmSettingRepository;

  public List<ConnectionInfo> filterTargetClients(NotificationDto notificationDto) {
    return connectionManager.getConnections().stream()
        .filter(connectionInfo -> switchEnableType(connectionInfo, notificationDto))
        .toList();
  }

  private boolean switchEnableType(ConnectionInfo connectionInfo, NotificationDto notificationDto) {
    //TODO 알람 설정 관련해서 테스트를 위해서 일단 전부 허용으로 가정함
//    AlarmSetting alarmSetting = alarmSettingRepository.findById(connectionInfo.getUserId())
//        .orElseThrow(
//            () -> new MplException(ErrorCode.USER_NOT_FOUND)
//        );
    //TODO 테스트 완료후 지워야 함
    User user = new User();
    AlarmSetting alarmSetting = new AlarmSetting(user);

    NotificationType type = notificationDto.type();
    return switch (type) {
      case NEW_FOLLOWER -> alarmSetting.getFollowAlarmEnabled();
      case PLAYLIST_SUBSCRIBED -> alarmSetting.getSubscribePlaylistAlarmEnable();
      case NEW_PLAYLIST_BY_FOLLOWING -> alarmSetting.getNewPlaylistFromFollowingAlarmEnabled();
      case ROLE_CHANGED -> alarmSetting.getPermissionChangeAlarmEnabled();
      case NEW_MESSAGE -> alarmSetting.getDmAlarmEnabled();
      default -> false;
    };
  }
}
