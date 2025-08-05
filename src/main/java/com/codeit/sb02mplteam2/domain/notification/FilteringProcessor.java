package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
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

  //TODO 브로드캐스트 : 추천 알람이 허용된 사람만 ConnectionInfo 리스트 가져오도록 해야함
  public List<ConnectionInfo> broadcastClients() {
    return connectionManager.getConnections();
  }
}
