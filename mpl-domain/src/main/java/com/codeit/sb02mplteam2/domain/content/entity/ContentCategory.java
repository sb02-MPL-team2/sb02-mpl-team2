package com.codeit.sb02mplteam2.domain.content.entity;
//TODO /3이 docker-compose.yml에도 존재해서 /3/3이 두 번 작성되는 문제 발생함
public enum ContentCategory {
  MOVIE("/discover/movie", "movie"),
  TV("/discover/tv", "tv"),
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
