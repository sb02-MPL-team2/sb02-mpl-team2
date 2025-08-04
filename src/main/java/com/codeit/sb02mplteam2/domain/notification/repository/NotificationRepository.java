package com.codeit.sb02mplteam2.domain.notification.repository;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

  List<Notification> findAllByReceiverId(Long receiverId);

  List<Notification> findAllByReceiverIdAndIdAfter(Long receiverId, Long idAfter);
}
