package com.codeit.sb02mplteam2.domain.content.entity;

public enum ContentCategory {
  MOVIE("/3/discover/movie"),
  TV("/3/discover/tv"),
  SPORTS("");

  private final String path;

  ContentCategory(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
