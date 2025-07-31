package com.codeit.sb02mplteam2.exception.user;

import com.codeit.sb02mplteam2.exception.ErrorCode;

public class UserNotFoundException extends UserException {
  public UserNotFoundException() {
    super(ErrorCode.USER_NOT_FOUND);
  }

  public static UserNotFoundException withId(Long userId) {
    UserNotFoundException exception = new UserNotFoundException();
    exception.addDetail("userId", userId);
    return exception;
  }
}
