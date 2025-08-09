package com.codeit.sb02mplteam2.exception.directmessage;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class DirectMessageException extends MplException {

  public DirectMessageException(ErrorCode errorCode) {
    super(errorCode);
  }

  public DirectMessageException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}