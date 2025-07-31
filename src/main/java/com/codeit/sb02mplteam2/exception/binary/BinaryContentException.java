package com.codeit.sb02mplteam2.exception.binary;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class BinaryContentException extends MplException {

  public BinaryContentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BinaryContentException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
