package com.codeit.sb02mplteam2.domain.notification.repository;

import com.codeit.sb02mplteam2.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

}
