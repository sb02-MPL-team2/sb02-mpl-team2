package com.codeit.sb02mplteam2.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
  NEW_FOLLOWER("새로운 팔로워", "님이 회원님을 팔로우하기 시작했습니다."),
  NEW_PLAYLIST_BY_FOLLOWING("새로운 재생목록", "님이 새로운 재생목록을 등록했습니다."),
  PLAYLIST_SUBSCRIBED("재생목록 구독", "님이 회원님의 재생목록을 구독했습니다."),
  NEW_MESSAGE("새로운 쪽지", "님으로부터 새로운 DM이 도착했습니다."),
  ROLE_CHANGED("새로운 쪽지", "회원님의 계정 권한이 변경되었습니다."),
  ASYNC_FAILED("비동기 작업 실패", "내부 작업 처리 중 오류가 발생했습니다.");

  private final String title;
  private final String messageTemplate;
}
