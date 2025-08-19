package com.codeit.sb02mplteam2.exception.content;

import com.codeit.sb02mplteam2.exception.ErrorCode;

public class ContentNotFoundException extends ContentException {
  public ContentNotFoundException() {
    super(ErrorCode.CONTENT_NOT_FOUND);
  }

  public static ContentNotFoundException withId(Long contentId) {
    ContentNotFoundException ex = new ContentNotFoundException();
    ex.addDetail("contentId", contentId);
    return ex;
  }
}