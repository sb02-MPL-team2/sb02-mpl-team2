package com.codeit.sb02mplteam2.domain.notification.controller;

import com.codeit.sb02mplteam2.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteById(@PathVariable Long notificationId) {
    notificationService.delete(notificationId);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> deleteAllByUserId(@PathVariable Long userId) {
    notificationService.deleteAllByUserId(userId);

    return ResponseEntity.noContent().build();
  }

}
