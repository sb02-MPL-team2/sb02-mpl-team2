package com.codeit.sb02mplteam2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MplException.class)
  public ResponseEntity<ErrorResponse> handleMplException(MplException e) {
    HttpStatus status = e.getErrorCode().getHttpStatus();
    return ResponseEntity
        .status(status)
        .body(ErrorResponse.fromMplException(e));
  }
}
