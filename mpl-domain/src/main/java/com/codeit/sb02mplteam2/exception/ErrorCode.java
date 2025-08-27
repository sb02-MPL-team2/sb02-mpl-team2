package com.codeit.sb02mplteam2.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
  USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 유저 이름입니다."),
  ADMIN_USER_NOT_FOUND(HttpStatus.FORBIDDEN, "관리자 정보를 찾을 수 없습니다."),
  //Alarm
  ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "알람을 찾을 수 없습니다."),
  // Server Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다."),

  // Content
  CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"콘텐츠를 찾을 수 없습니다."),

  // Binary
  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"바이너리 콘텐츠를 찾을 수 없습니다."),
  FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
  FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."),
  FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
  FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),
  STORAGE_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스토리지 초기화 실패"),
  FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),
  FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다."),

  //Playlist
  PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND,"플레이리스트를 찾을 수 없습니다."),
  SUBSCRIBE_NOT_FOUND(HttpStatus.NOT_FOUND,"구독 정보를 찾을 수 없습니다."),

  // Security
  INVALID_TOKEN_SECRET(HttpStatus.UNAUTHORIZED,"유효하지 않은 시크릿입니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다."),
  NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
  UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
  BLACKLIST_TOKEN(HttpStatus.FORBIDDEN, "블랙리스트에 있는 토큰입니다."),
  NO_ACCESS_RIGHT(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

  // OAuth
  UNKNOWN_PROVIDER(HttpStatus.NOT_FOUND, "알 수 없는 OAuth 제공자입니다."),
  MISSING_REQUIRED_OAUTH_INFO(HttpStatus.BAD_REQUEST, "필수 OAuth 사용자 정보를 찾을 수 없습니다."),

  // Email
  EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),

  //Review
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"리뷰를 찾을 수 없습니다."),

  //Follow
  SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "사용자는 자신을 팔로우할 수 없습니다."),
  FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 관계를 찾을 수 없습니다."),
  FOLLOW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 팔로우하고 있습니다."),

  //DirectMessage

  //DirectMessageChannel
  DIRECT_MESSAGE_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "디엠 채널을 찾을 수 없습니다"),
  
  //LiveWatch
  LIVE_WATCH_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "실시간 같이 보기 방을 찾을 수 없습니다."),
  LIVE_WATCH_USER_NOT_IN_ROOM(HttpStatus.FORBIDDEN, "실시간 같이 보기 방에 참여하지 않은 사용자입니다."),
  LIVE_WATCH_MESSAGE_CURSOR_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 채팅 메시지 조회 커서입니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
