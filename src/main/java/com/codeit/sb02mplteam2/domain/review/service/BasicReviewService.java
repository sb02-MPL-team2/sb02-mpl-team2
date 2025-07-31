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
import com.codeit.sb02mplteam2.exception.MplException;
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
    User user = userRepository.findById(request.userId()).orElseThrow(() ->
        new MplException("유저를 찾을 수 없습니다."));
    Content content = contentRepository.findById(request.contentId()).orElseThrow(
        () -> new MplException("Content를 찾을 수 없습니다.")
    );

    Review review = new Review(user, content, request.rating(), request.comment());
    reviewRepository.save(review);
    return ReviewDto.from(review);
  }

  @Override
  public ReviewDto findById(Long reviewId) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new MplException("리뷰가 존재하지 않습니다.")
    );
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
        () -> new MplException("리뷰가 존재하지 않습니다.")
    );
    reviewRepository.delete(review);
  }

  @Override
  public ReviewDto update(Long reviewId, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new MplException("리뷰가 존재하지 않습니다.")
    );
    review.update(request.newRating(), request.newComment());
    reviewRepository.save(review);

    return ReviewDto.from(review);
  }
}
