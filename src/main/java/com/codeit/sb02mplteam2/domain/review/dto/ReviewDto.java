package com.codeit.sb02mplteam2.domain.review.dto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import lombok.Builder;

@Builder
public record ReviewDto(
    Long id,
    ContentResponseDto content,
    UserSlimDto author,
    int rating,
    String comment
) {

  public static ReviewDto from(Review review, UserSlimDto userSlimDto, ContentResponseDto contentResponseDto) {
    return ReviewDto.builder()
        .id(review.getId())
        .content(contentResponseDto)
        .author(userSlimDto)
        .rating(review.getRating())
        .comment(review.getComment())
        .build();
  }
}
