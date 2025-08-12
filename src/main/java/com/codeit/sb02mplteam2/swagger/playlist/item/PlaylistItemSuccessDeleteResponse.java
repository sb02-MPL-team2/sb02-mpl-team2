package com.codeit.sb02mplteam2.swagger.playlist.item;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "204", description = "PlayList Item이 성공적으로 삭제됨"
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PlaylistItemSuccessDeleteResponse {

}
