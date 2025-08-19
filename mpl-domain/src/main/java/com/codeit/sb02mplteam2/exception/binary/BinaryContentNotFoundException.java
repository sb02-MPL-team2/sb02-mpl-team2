package com.codeit.sb02mplteam2.exception.binary;

import com.codeit.sb02mplteam2.exception.ErrorCode;

public class BinaryContentNotFoundException extends BinaryContentException {
  public BinaryContentNotFoundException() {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND);
  }

  public static BinaryContentNotFoundException withId(Long binaryContentId) {
    BinaryContentNotFoundException ex = new BinaryContentNotFoundException();
    ex.addDetail("binaryContentId", binaryContentId);
    return ex;
  }
}