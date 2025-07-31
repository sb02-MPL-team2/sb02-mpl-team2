package com.codeit.sb02mplteam2.domain.review.service;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.review.repository.ReviewRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.MplException;
import com.codeit.sb02mplteam2.exception.content.ContentException;
import com.codeit.sb02mplteam2.exception.review.ReviewException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicReviewService implements ReviewService{

  private final UserRepository userRepository;
  private final ContentRepository contentRepository;
  private final ReviewRepository reviewRepository;

  @Override
  public ReviewDto create(ReviewCreateRequest request) {
    User user = userRepository.findById(request.userId()).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));
    Content content = contentRepository.findById(request.contentId()).orElseThrow(
        () -> new ContentException(ErrorCode.CONTENT_NOT_FOUND));

    Review review = new Review(user, content, request.rating(), request.comment());
    reviewRepository.save(review);
    return ReviewDto.from(review);
  }

  @Override
  public ReviewDto findById(Long reviewId) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    return ReviewDto.from(review);
  }

  @Override
  public List<ReviewDto> findAllByUserId(Long userId) {
    List<Review> reviewList = reviewRepository.findAllByUserId(userId);
    return reviewList.stream().map(ReviewDto::from).toList();
  }

  @Override
  public List<ReviewDto> findAllByContentId(Long contentId) {
    List<Review> reviewList = reviewRepository.findAllByContentId(contentId);
    return reviewList.stream().map(ReviewDto::from).toList();
  }

  @Override
  public void delete(Long id) {
    Review review = reviewRepository.findById(id).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    reviewRepository.delete(review);
  }

  @Override
  public ReviewDto update(Long reviewId, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    review.update(request.newRating(), request.newComment());
    reviewRepository.save(review);

    return ReviewDto.from(review);
  }
}
