package com.codeit.sb02mplteam2.domain.notification.repository;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

  @Query("""
      SELECT n FROM Notification n
                  WHERE (n.receiverId = :receiverId OR n.receiverId IS NULL)
                        AND n.createdAt > :lastEventTime
                    ORDER BY n.createdAt ASC
      """)
  List<Notification> findUserNotificationAfter(@Param("receiverId") Long receiverId,
      @Param("lastEventTime") LocalDateTime lastEventTime);

  List<Notification> findAllByReceiverIdAndCreatedAtAfterOrderByCreatedAtAsc(Long receiverId, LocalDateTime lastEventTime);

  @Modifying
  @Query("DELETE FROM Notification n WHERE n.createdAt < :current")
  void deleteByCreatedAtBefore(LocalDateTime current);

  @Modifying
  @Query("DELETE FROM Notification n WHERE n.receiverId = :receiverId")
  void deleteAllByReceiverId(@Param("receiverId") Long userId);

  Optional<Notification> findByTypeAndTargetIdAndCreatedAtAfter(NotificationType type, Long targetId, LocalDateTime createdAtAfter);
}
