package com.codeit.sb02mplteam2.exception.livewatch;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class LiveChatException extends MplException {

  public LiveChatException(ErrorCode errorCode) {
    super(errorCode);
  }

  public LiveChatException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}