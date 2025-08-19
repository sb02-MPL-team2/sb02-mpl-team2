package com.codeit.sb02mplteam2.domain.review.controller;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import com.codeit.sb02mplteam2.domain.review.service.ReviewService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.swagger.ReviewApi;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController implements ReviewApi {

  private final ReviewService reviewService;

  @Override
  @PostMapping
  public ResponseEntity<ReviewDto> create(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @RequestBody ReviewCreateRequest request) {
    ReviewDto reviewDto = reviewService.create(userDetails.getUserDto().id(),request);
    return ResponseEntity.status(HttpStatus.CREATED).body(reviewDto);
  }

  @Override
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @PathVariable Long reviewId) {
    reviewService.delete(userDetails.getUserDto().id(), reviewId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> update(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @PathVariable Long reviewId,
      @RequestBody ReviewUpdateRequest request) {
    ReviewDto reviewDto = reviewService.update(userDetails.getUserDto().id(), reviewId, request);

    return ResponseEntity.ok(reviewDto);
  }

  @Override
  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> findById(@PathVariable Long reviewId) {
    ReviewDto reviewDto = reviewService.findById(reviewId);

    return ResponseEntity.ok(reviewDto);
  }

  @Override
  @GetMapping("/users/{userId}")
  public ResponseEntity<List<ReviewDto>> findAllByUserId(@PathVariable Long userId) {
    List<ReviewDto> reviewDtoList = reviewService.findAllByUserId(userId);
    return ResponseEntity.ok(reviewDtoList);
  }

  @Override
  @GetMapping("/content/{contentId}")
  public ResponseEntity<List<ReviewDto>> findAllByContentId(@PathVariable Long contentId) {
    List<ReviewDto> reviewDtoList = reviewService.findAllByContentId(contentId);
    return ResponseEntity.ok(reviewDtoList);
  }
}
