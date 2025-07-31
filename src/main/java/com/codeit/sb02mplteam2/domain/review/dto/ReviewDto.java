package com.codeit.sb02mplteam2.domain.review.dto;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import lombok.Builder;

@Builder
public record ReviewDto(
    Long id,
    Content content,
    UserSlimDto author,
    int rating,
    String comment
) {

  public static ReviewDto from(Review review) {
    User user = review.getUser();
    BinaryContent profile = user.getProfile();
    String profileUrl = null;
    if (profile != null) {
      profileUrl = profile.getUrl();
    }
    UserSlimDto userSlimDto = new UserSlimDto(user.getId(), profileUrl, user.getUsername());

    return ReviewDto.builder()
        .id(review.getId())
        .content(review.getContent())
        .author(userSlimDto)
        .rating(review.getRating())
        .comment(review.getComment())
        .build();
  }
}
