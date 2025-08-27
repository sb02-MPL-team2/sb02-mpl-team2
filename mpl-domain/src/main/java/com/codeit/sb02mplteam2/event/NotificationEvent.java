package com.codeit.sb02mplteam2.event;

import com.codeit.sb02mplteam2.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // JSON 역직렬화를 위해 Setter 또는 AllArgsConstructor가 필요합니다.
@NoArgsConstructor // Jackson이 JSON을 객체로 변환할 때 기본 생성자가 필요합니다.
@AllArgsConstructor
public class NotificationEvent {
  private Long receiverId; // 알림 받을 사용자 ID
  private NotificationType notificationType;
  private Long targetId; // 알림과 관련된 플레이리스트 ID, DM ID
  private Long publisherId; //이벤트를 발생시킨 사용자 ID
}
