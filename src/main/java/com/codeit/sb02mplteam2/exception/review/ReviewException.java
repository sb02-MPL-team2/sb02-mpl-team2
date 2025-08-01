package com.codeit.sb02mplteam2.exception.review;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class ReviewException extends MplException {

  public ReviewException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ReviewException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
