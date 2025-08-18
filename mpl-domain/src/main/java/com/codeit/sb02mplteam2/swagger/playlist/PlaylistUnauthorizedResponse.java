package com.codeit.sb02mplteam2.swagger.playlist;

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
    description = "PlayList 관련 권한이 없습니다.",
    content = @Content(
        schema = @Schema(implementation = ErrorResponse.class),
        examples = @ExampleObject(
            value = """
                    {
                      "timestamp": "2025-07-31T00:00:00.0000000",
                      "code": "UNAUTHORIZED",
                      "message": "플레이리스트에 대한 권한이 없습니다.",
                      "details": {},
                      "exceptionType": "PlaylistException",
                      "status": 401
                    }
                    """
        )
)
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PlaylistUnauthorizedResponse {

}
