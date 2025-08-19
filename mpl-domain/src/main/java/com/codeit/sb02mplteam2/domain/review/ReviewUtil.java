package com.codeit.sb02mplteam2.domain.review;

import com.codeit.sb02mplteam2.domain.binaryContent.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // private 생성자로 인스턴스화 방지
public class ReviewUtil {

  public static UserSlimDto toUserSlimDto(Review review) {
    User user = review.getUser();
    BinaryContent userProfile = user.getProfile();
    String profileUrl = null;
    if (userProfile != null) {
      profileUrl = userProfile.getUrl();
    }
    return new UserSlimDto(user.getId(), profileUrl, user.getUsername());
  }

  //Content DTO 변환 기능
  public static ContentResponseDto toResponseDto(Content content) {
    return ContentResponseDto.builder()
        .id(content.getId())
        .title(content.getTitle())
        .description(content.getDescription())
        .category(content.getCategory().toString())
        .imageUrl(content.getImageUrl())
        .build();
  }
}
