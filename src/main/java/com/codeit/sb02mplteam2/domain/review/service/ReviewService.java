package com.codeit.sb02mplteam2.domain.review.service;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import java.util.List;

public interface ReviewService {

  ReviewDto create(ReviewCreateRequest request);

  List<ReviewDto> findAllByUserId(Long userId);

  List<ReviewDto> findAllByContentId(Long contentId);

  void delete(Long id);
}
