package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.domain.notification.entity.ConnectionInfo;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FilteringProcessor {

  private final ConnectionManager connectionManager;

  public ConnectionInfo filterTargetClient(Long receiverId) {
    return connectionManager.getConnection(receiverId);
  }

  public List<ConnectionInfo> filterTargetClients(Set<Long> receiverIds) {
    return connectionManager.getConnectionIn(receiverIds);
  }
}
