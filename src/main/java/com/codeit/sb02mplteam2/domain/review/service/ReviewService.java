package com.codeit.sb02mplteam2.domain.review.service;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import java.util.List;

public interface ReviewService {

  ReviewDto create(Long userId, ReviewCreateRequest request);

  ReviewDto findById(Long reviewId);

  List<ReviewDto> findAllByUserId(Long userId);

  List<ReviewDto> findAllByContentId(Long contentId);

  void delete(Long userId, Long reviewId);

  ReviewDto update(Long userId, Long reviewId, ReviewUpdateRequest request);
}
