package com.codeit.sb02mplteam2.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class MplException extends RuntimeException {

  private final LocalDateTime timestamp;
  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public MplException(String message) {
    super(message);
    this.timestamp = LocalDateTime.now();
    this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    this.details = new HashMap<>();
  }

  public MplException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = new HashMap<>();
  }

  public MplException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.timestamp = LocalDateTime.now();
    this.errorCode = errorCode;
    this.details = new HashMap<>();
  }

  public MplException(ErrorCode errorCode, Map<String, Object> details) {
    this(errorCode);
    this.details.putAll(details);
  }

  public MplException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
    this(errorCode, cause);
    this.details.putAll(details);
  }

  public void addDetail(String key, Object value) {
    this.details.put(key, value);
  }

}
