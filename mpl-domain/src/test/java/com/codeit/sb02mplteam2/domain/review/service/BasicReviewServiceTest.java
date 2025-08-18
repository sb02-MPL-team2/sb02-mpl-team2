package com.codeit.sb02mplteam2.domain.review.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.sb02mplteam2.domain.content.entity.Content;
import com.codeit.sb02mplteam2.domain.content.entity.ContentCategory;
import com.codeit.sb02mplteam2.domain.content.repository.ContentRepository;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import com.codeit.sb02mplteam2.domain.review.entity.Review;
import com.codeit.sb02mplteam2.domain.review.repository.ReviewRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicReviewServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private BasicReviewService reviewService;

  private User user;
  private Content content;
  private Review review;
  private final int rating = 5;
  private final String comment = "comment";

  @BeforeEach
  void setUp() {
    user = new User();
    ReflectionTestUtils.setField(user, "id", 1L);
    userRepository.save(user);
    content = new Content("test", ContentCategory.MOVIE);
    contentRepository.save(content);
    review = new Review(user, content, rating, comment);
    reviewRepository.save(review);
  }

  @Test
  void create() {
    //given
    ReviewCreateRequest request = new ReviewCreateRequest( 1L, rating, comment);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(contentRepository.findById(1L)).thenReturn(Optional.of(content));
    //when
    ReviewDto reviewDto = reviewService.create(1L, request);
    //then
    assertAll(
        () -> assertEquals(rating, reviewDto.rating()),
        () -> assertEquals(comment, reviewDto.comment())
    );
  }

  @Test
  void findById() {
    //given
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
    //when
    ReviewDto reviewDto = reviewService.findById(1L);
    //then
    assertAll(
        () -> assertEquals(rating, reviewDto.rating()),
        () -> assertEquals(comment, reviewDto.comment())
    );
  }

  @Test
  void findAllByUserId() {
    //given
    when(reviewRepository.findAllByUserId(1L)).thenReturn(List.of(review));
    //when
    List<ReviewDto> reviewDtoList = reviewService.findAllByUserId(1L);
    //then
    assertAll(
        () -> assertEquals(1, reviewDtoList.size()),
        () -> assertEquals(rating, reviewDtoList.get(0).rating()),
        () -> assertEquals(comment, reviewDtoList.get(0).comment())
    );
  }

  @Test
  void findAllByContentId() {
    //given
    when(reviewRepository.findAllByContentId(1L)).thenReturn(List.of(review));
    //when
    List<ReviewDto> reviewDtoList = reviewService.findAllByContentId(1L);
    //then
    assertAll(
        () -> assertEquals(1, reviewDtoList.size()),
        () -> assertEquals(rating, reviewDtoList.get(0).rating()),
        () -> assertEquals(comment, reviewDtoList.get(0).comment())
    );
  }

  @Test
  void delete() {
    //given
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
    //when
    reviewService.delete(1L, 1L);
    //then
    verify(reviewRepository).delete(review);
  }

  @Test
  void update() {
    //given
    ReviewUpdateRequest request = new ReviewUpdateRequest(3, "newComment");
    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
    //when
    ReviewDto reviewDto = reviewService.update(1L, 1L, request);
    //then
    assertAll(
        () -> assertEquals(3, reviewDto.rating()),
        () -> assertEquals("newComment", reviewDto.comment())
    );
  }
}