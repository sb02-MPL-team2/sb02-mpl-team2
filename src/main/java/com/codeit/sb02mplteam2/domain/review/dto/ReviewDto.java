package com.codeit.sb02mplteam2.domain.review.dto;

import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;

public record ReviewDto(
    Long id,
    UserSlimDto author,
    Double rating,
    String comment
) {

}
