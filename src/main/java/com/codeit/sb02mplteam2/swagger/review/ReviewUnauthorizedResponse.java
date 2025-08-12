package com.codeit.sb02mplteam2.swagger.review;

import com.codeit.sb02mplteam2.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "401",
    description = "review 관련 권한이 없습니다.",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                    {
                      "timestamp": "2025-07-31T00:00:00.0000000",
                      "code": "UNAUTHORIZED",
                      "message": "리뷰에 대한 권한이 없습니다.",
                      "details": {},
                      "exceptionType": "ReviewException",
                      "status": 401
                    }
                    """
        )
)
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReviewUnauthorizedResponse {

}
