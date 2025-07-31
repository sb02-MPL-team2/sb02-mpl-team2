package com.codeit.sb02mplteam2.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  // User
  USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

  // Server Error
  INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),

  // Content
  CONTENT_NOT_FOUND("콘텐츠를 찾을 수 없습니다."),

  // Binary
  BINARY_CONTENT_NOT_FOUND("바이너리 콘텐츠를 찾을 수 없습니다.");

  private final String message;

  ErrorCode(String message){
    this.message = message;
  }
}
