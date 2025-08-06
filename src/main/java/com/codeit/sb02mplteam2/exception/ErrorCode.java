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

  // Server Error
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다."),

  // Content
  CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"콘텐츠를 찾을 수 없습니다."),

  // Binary
  BINARY_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND,"바이너리 콘텐츠를 찾을 수 없습니다."),
  FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다."),
  FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다."),

  //Playlist
  PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND,"플레이리스트를 찾을 수 없습니다."),
  SUBSCRIBE_NOT_FOUND(HttpStatus.NOT_FOUND,"구독 정보를 찾을 수 없습니다."),

  //Review
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"리뷰를 찾을 수 없습니다."),

  //Follow
  FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우 관계를 찾을 수 없습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
