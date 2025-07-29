package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.content.dto.ContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Content", description = "Content API")
public interface ContentApi {

  @Operation(summary = "Content 상세 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Content 상세 조회 성공",
          content = @Content(schema = @Schema(implementation = ContentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Content 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Content with id {contentId} not found"))
      )
  })
  ResponseEntity<ContentDto> findById(
    @Parameter(description = "조회할 Content ID") Long contentId
  );

  @Operation(summary = "전체 Content 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Conent 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContentDto.class)))
      )
  })
  ResponseEntity<List<ContentDto>> findAll();

  @Operation(summary = "카테고리별 Content 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "카테고리별 Content 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = ContentDto.class)))
      )
  })
  ResponseEntity<List<ContentDto>> findByCategory(
      @Parameter(description = "조회할 카테고리") String category
  );

  @Operation(summary = "Content 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Content 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "Content 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Content with id {contentId} not found"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 content ID") Long contentId
  );
}
