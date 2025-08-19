package com.codeit.sb02mplteam2.exception.directmessage;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class DirectMessageChannelException extends MplException {

  public DirectMessageChannelException(ErrorCode errorCode) {
    super(errorCode);
  }

  public DirectMessageChannelException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}