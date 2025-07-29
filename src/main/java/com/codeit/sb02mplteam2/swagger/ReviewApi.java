package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewCreateRequest;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import com.codeit.sb02mplteam2.domain.review.dto.ReviewUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Review", description = "Review API")
public interface ReviewApi {

  @Operation(summary = "Review 생성")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Review 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User 또는 Content를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Content | User with id {ContentId | UserId} not found"))
      ),
  })
  ResponseEntity<ReviewDto> create(
      @Parameter(
          description = "Review 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) ReviewCreateRequest ReviewCreateRequest
  );

  @Operation(summary = "Review 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Review가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "Review를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Review with id {ReviewId} not found"))
      ),
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 Review ID") Long reviewId
  );

  @Operation(summary = "Message 내용 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Review가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Review를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Review with id {ReviewId} not found"))
      ),
  })
  ResponseEntity<ReviewDto> update(
      @Parameter(description = "수정할 Review ID") Long reviewId,
      @Parameter(description = "수정할 Review 내용") ReviewUpdateRequest request
  );

  @Operation(summary = "Review 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "ReviewDto 단건 조회 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      )
  })
  ResponseEntity<ReviewDto> findById(
      @Parameter(description = "조회할 Review ID") Long reviewId
  );

  @Operation(summary = "Review 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Review 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      )
  })
  ResponseEntity<List<ReviewDto>> findAllByUserId(
      @Parameter(description = "조회할 Channel ID") Long userId
  );

  @Operation(summary = "Review 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Review 목록 조회 성공",
          content = @Content(schema = @Schema(implementation = ReviewDto.class))
      )
  })
  ResponseEntity<List<ReviewDto>> findAllByContentId(
      @Parameter(description = "조회할 Channel ID") Long contentId
  );





}
