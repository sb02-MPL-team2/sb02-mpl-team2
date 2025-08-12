package com.codeit.sb02mplteam2.domain.review.service;

import static com.codeit.sb02mplteam2.domain.review.ReviewUtil.toResponseDto;
import static com.codeit.sb02mplteam2.domain.review.ReviewUtil.toUserSlimDto;

import com.codeit.sb02mplteam2.domain.content.dto.content.ContentResponseDto;
import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.review.repository.ReviewRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserSlimDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.content.ContentException;
import com.codeit.sb02mplteam2.exception.review.ReviewException;
import com.codeit.sb02mplteam2.exception.user.UserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicReviewService implements ReviewService{

  private final UserRepository userRepository;
  private final ContentRepository contentRepository;
  private final ReviewRepository reviewRepository;

  @Override
  @Transactional
  public ReviewDto create(Long userId, ReviewCreateRequest request) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserException(ErrorCode.USER_NOT_FOUND));
    Content content = contentRepository.findById(request.contentId()).orElseThrow(
        () -> new ContentException(ErrorCode.CONTENT_NOT_FOUND));

    Review review = new Review(user, content, request.rating(), request.comment());
    reviewRepository.save(review);

    UserSlimDto userSlimDto = toUserSlimDto(review);
    ContentResponseDto responseDto = toResponseDto(content);
    return ReviewDto.from(review, userSlimDto, responseDto);
  }

  @Override
  @Transactional(readOnly = true)
  public ReviewDto findById(Long reviewId) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    UserSlimDto userSlimDto = toUserSlimDto(review);
    ContentResponseDto responseDto = toResponseDto(review.getContent());
    return ReviewDto.from(review, userSlimDto, responseDto);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReviewDto> findAllByUserId(Long userId) {
    List<Review> reviewList = reviewRepository.findAllByUserId(userId);
    return reviewList.stream().map(review -> {
      UserSlimDto userSlimDto = toUserSlimDto(review);
      ContentResponseDto responseDto = toResponseDto(review.getContent());
      return ReviewDto.from(review, userSlimDto, responseDto);
    }).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReviewDto> findAllByContentId(Long contentId) {
    List<Review> reviewList = reviewRepository.findAllByContentId(contentId);
    return reviewList.stream().map(review -> {
      UserSlimDto userSlimDto = toUserSlimDto(review);
      ContentResponseDto responseDto = toResponseDto(review.getContent());
      return ReviewDto.from(review, userSlimDto, responseDto);
    }).toList();
  }

  @Override
  @Transactional
  public void delete(Long userId, Long reviewId) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ErrorCode.UNAUTHORIZED);
    }
    reviewRepository.delete(review);
  }

  @Override
  @Transactional
  public ReviewDto update(Long userId, Long reviewId, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(reviewId).orElseThrow(
        () -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));

    if (!review.getUser().getId().equals(userId)) {
      throw new ReviewException(ErrorCode.UNAUTHORIZED);
    }

    review.update(request.newRating(), request.newComment());
    reviewRepository.save(review);

    UserSlimDto userSlimDto = toUserSlimDto(review);
    ContentResponseDto responseDto = toResponseDto(review.getContent());
    return ReviewDto.from(review, userSlimDto, responseDto);
  }
}
