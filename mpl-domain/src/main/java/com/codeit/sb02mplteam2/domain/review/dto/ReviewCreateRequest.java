package com.codeit.sb02mplteam2.domain.review.dto;

public record ReviewCreateRequest(
  Long contentId,
  int rating,
  String comment
) {

}
