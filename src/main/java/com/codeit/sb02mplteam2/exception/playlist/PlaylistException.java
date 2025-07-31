package com.codeit.sb02mplteam2.exception.playlist;

import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;

public class PlaylistException extends MplException {

  public PlaylistException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PlaylistException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
