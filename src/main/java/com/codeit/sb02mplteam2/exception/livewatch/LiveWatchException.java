package com.codeit.sb02mplteam2.exception.livewatch;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class LiveWatchException extends MplException {

  public LiveWatchException(ErrorCode errorCode) {
    super(errorCode);
  }

  public LiveWatchException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}