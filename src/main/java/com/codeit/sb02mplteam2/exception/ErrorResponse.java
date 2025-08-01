package com.codeit.sb02mplteam2.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    int status,
    LocalDateTime timestamp,
    String message,
    Map<String, Object> details
) {

  public static ErrorResponse fromMplException(MplException e) {
    return new ErrorResponse(
        e.getErrorCode().getHttpStatus().value(),
        e.getTimestamp(),
        e.getErrorCode().getMessage(),
        e.getDetails());
  }

}
