package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
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

  public ConnectionInfo filterTargetClient(Long receiverId) {
    return connectionManager.getConnection(receiverId);
  }

  public List<ConnectionInfo> filterTargetClients(Set<Long> receiverIds) {
    return connectionManager.getConnectionIn(receiverIds);
  }

  //TODO 브로드캐스트 : 알고리즘 개선 가능해보임
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
