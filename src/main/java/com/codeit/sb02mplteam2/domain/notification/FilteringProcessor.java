package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilteringProcessor {

  private final ConnectionManager connectionManager;
  private final AlarmSettingRepository alarmSettingRepository;

  public ConnectionInfo filtering(NotificationType type, ConnectionInfo connectionInfo) {
    Long userId = connectionInfo.getUserId();
    AlarmSetting alarmSetting = alarmSettingRepository.findByUserId(userId).orElseThrow(
        () -> new MplException(ErrorCode.USER_NOT_FOUND)
    );
    Boolean enabled = switch (type) {
      case NEW_MESSAGE -> alarmSetting.getDmAlarmEnabled();
      case NEW_FOLLOWER -> alarmSetting.getFollowAlarmEnabled();
      case NEW_PLAYLIST_BY_FOLLOWING -> alarmSetting.getNewPlaylistFromFollowingAlarmEnabled();
      case PLAYLIST_SUBSCRIBED -> alarmSetting.getSubscribePlaylistAlarmEnable();
      case ROLE_CHANGED -> alarmSetting.getPermissionChangeAlarmEnabled();
      case BROADCAST_TODAY_PLAYLIST -> alarmSetting.getRecommendPlaylistAlarmEnabled();
      default -> false;
    };
    if (enabled) {
      return connectionInfo;
    }
    return null;
  }

  public ConnectionInfo filterTargetClient(Long receiverId) {
    return connectionManager.getConnection(receiverId);
  }

  public List<ConnectionInfo> filterTargetClients(Set<Long> receiverIds) {
    return connectionManager.getConnectionIn(receiverIds);
  }

  //TODO 브로드캐스트: broadcastClients에서 N+1 쿼리 문제 발생 가능. AlarmSetting을 미리 batch로 조회하여 성능을 개선할 수 있음. (See issue #123)
  public List<ConnectionInfo> broadcastClients() {
    return connectionManager.getConnections().stream()
        .filter(connectionInfo -> {
          Long userId = connectionInfo.getUserId();
          AlarmSetting alarmSetting = alarmSettingRepository.findByUserId(userId).orElseThrow(
              () -> new MplException(ErrorCode.USER_NOT_FOUND)
          );
          return alarmSetting.getRecommendPlaylistAlarmEnabled();
        }).toList();
  }
}
