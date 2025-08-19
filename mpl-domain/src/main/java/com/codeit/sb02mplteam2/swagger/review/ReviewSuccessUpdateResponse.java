package com.codeit.sb02mplteam2.swagger.review;

import com.codeit.sb02mplteam2.domain.review.dto.ReviewDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "200", description = "Review가 성공적으로 수정됨",
    content = @Content(schema = @Schema(implementation = ReviewDto.class))
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReviewSuccessUpdateResponse {

}
