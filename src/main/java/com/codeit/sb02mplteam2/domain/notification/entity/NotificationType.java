package com.codeit.sb02mplteam2.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
  NEW_MESSAGE("쪽지", "%s님으로부터 새로운 DM이 도착했습니다."),
  NEW_PLAYLIST_BY_FOLLOWING("재생목록", "%s님이 새로운 재생목록을 등록했습니다."),
  PLAYLIST_SUBSCRIBED("재생목록 구독", "%s님이 회원님의 재생목록을 구독했습니다."),
  NEW_FOLLOWER("팔로워", "%s님이 회원님을 팔로우하기 시작했습니다."),
  ROLE_CHANGED("권한변경", "회원님의 계정 권한이 변경되었습니다."),
  ASYNC_FAILED("비동기 작업 실패", "내부 작업 처리 중 오류가 발생했습니다."),

  BROADCAST_TODAY_PLAYLIST("브로드캐스트1", "오늘의 추천 플레이리스트");

  private final String title;
  private final String messageTemplate;

  public static String toTitle(String publisherName, String template) {
    return String.format(template, publisherName);
  }
}
