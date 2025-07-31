package com.codeit.sb02mplteam2.exception.content;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class ContentException extends MplException {

  public ContentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public ContentException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}