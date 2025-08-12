package com.codeit.sb02mplteam2.swagger.playlist.item;


import com.codeit.sb02mplteam2.domain.playlist.dto.PlaylistDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponse(
    responseCode = "201", description = "Content가 성공적으로 추가됨",
    content = @Content(schema = @Schema(implementation = PlaylistDto.class))
)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ItemSuccessInsertResponse {

}
