package com.codeit.sb02mplteam2.exception.user;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import java.util.Map;

public class UserException extends MplException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }

  public UserException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  public UserException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
