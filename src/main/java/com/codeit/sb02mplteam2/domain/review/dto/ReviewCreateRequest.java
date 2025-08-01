package com.codeit.sb02mplteam2.domain.review.dto;

public record ReviewCreateRequest(
  Long userId,
  Long contentId,
  int rating,
  String comment
) {

}
