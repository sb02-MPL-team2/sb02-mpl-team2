package com.codeit.sb02mplteam2.swagger.content;

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
    responseCode = "404",
    description = "콘텐츠 정보 없음",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                {
                  "timestamp": "2025-07-31T00:00:00.0000000",
                  "code": "Content Not Found",
                  "message": "콘텐츠를 찾을 수 없습니다.",
                  "details": {},
                  "exceptionType": "ContentException",
                  "status": 404
                }
                """
        )
    )
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ContentNotFoundResponse {

}
