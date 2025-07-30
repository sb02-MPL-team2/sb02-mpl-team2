package com.codeit.sb02mplteam2.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 및 비밀번호 관련 API")
public interface AuthApi {

//  @Operation(
//      summary = "비밀번호 초기화 요청 (비밀번호 분실 시)",
//      description = "사용자가 로그인을 할 수 없는 경우, 가입 시 사용한 이메일 주소를 통해 임시 비밀번호 발송을 요청합니다."
//  )
//  @ApiResponses(value = {
//      @ApiResponse(
//          responseCode = "200", description = "비밀번호 초기화 요청이 성공적으로 처리되었습니다."
//      ),
//      @ApiResponse(
//          responseCode = "404", description = "가입되지 않은 이메일 주소일 경우",
//          content = @Content(examples = @ExampleObject(value = "존재하지 않는 사용자입니다."))
//      )
//  })
//  ResponseEntity<String> requestPasswordReset(
//      @Parameter(
//          description = "비밀번호 초기화를 요청할 사용자 이메일 정보",
//          required = true,
//      )
//  )
}
