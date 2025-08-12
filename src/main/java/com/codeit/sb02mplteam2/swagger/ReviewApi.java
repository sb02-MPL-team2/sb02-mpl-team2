package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.swagger.content.ContentNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewNotFoundResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewSuccessCreationResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewSuccessDeleteResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewSuccessRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewSuccessSingleRetrievalResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewSuccessUpdateResponse;
import com.codeit.sb02mplteam2.swagger.review.ReviewUnauthorizedResponse;
import com.codeit.sb02mplteam2.swagger.user.UserNotFoundResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Review", description = "Review API")
public interface ReviewApi {

  @Operation(summary = "Review 생성")
  @ReviewSuccessCreationResponse
  @UserNotFoundResponse
  @ContentNotFoundResponse
  ResponseEntity<ReviewDto> create(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(
          description = "Review 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      )
      ReviewCreateRequest request
  );

  @Operation(summary = "Review 삭제")
  @ReviewSuccessDeleteResponse
  @ReviewNotFoundResponse
  @ReviewUnauthorizedResponse
  ResponseEntity<Void> delete(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(description = "삭제할 Review ID") Long reviewId
  );

  @Operation(summary = "Review 내용 수정")
  @ReviewSuccessUpdateResponse
  @ReviewNotFoundResponse
  @ReviewUnauthorizedResponse
  ResponseEntity<ReviewDto> update(
      @AuthenticationPrincipal MplUserDetails userDetails,
      @Parameter(description = "수정할 Review ID") Long reviewId,
      @Parameter(description = "수정할 Review 내용") ReviewUpdateRequest request
  );

  @Operation(summary = "Review 단건 조회")
  @ReviewNotFoundResponse
  @ReviewSuccessSingleRetrievalResponse
  ResponseEntity<ReviewDto> findById(
      @Parameter(description = "조회할 Review ID") Long reviewId
  );

  @Operation(summary = "Review 목록 조회")
  @ReviewSuccessRetrievalResponse
  ResponseEntity<List<ReviewDto>> findAllByUserId(
      @Parameter(description = "조회할 Channel ID") Long userId
  );

  @Operation(summary = "Review 목록 조회")
  @ReviewSuccessRetrievalResponse
  ResponseEntity<List<ReviewDto>> findAllByContentId(
      @Parameter(description = "조회할 Channel ID") Long contentId
  );
}
