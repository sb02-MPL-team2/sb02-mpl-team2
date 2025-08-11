package com.codeit.sb02mplteam2.domain.content.entity;

public enum ContentCategory {
  MOVIE("/3/discover/movie", "movie"),
  TV("/3/discover/tv", "tv"),
  SPORTS("", "sports");

  private final String path;
  private final String metricKey;

  ContentCategory(String path, String metricKey) {
    this.path = path;
    this.metricKey = metricKey;
  }

  public String getPath() {
    return path;
  }

  public String getMetricKey() {
    return metricKey;
  }
}
