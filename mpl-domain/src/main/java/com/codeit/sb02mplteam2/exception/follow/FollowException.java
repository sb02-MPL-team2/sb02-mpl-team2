package com.codeit.sb02mplteam2.exception.follow;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class FollowException extends MplException {

  public FollowException(ErrorCode errorCode) {
    super(errorCode);
  }

  public FollowException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
