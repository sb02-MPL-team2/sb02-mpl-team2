package com.codeit.sb02mplteam2.domain.auth.controller;

import com.codeit.sb02mplteam2.domain.auth.dto.PasswordForgotRequest;
import com.codeit.sb02mplteam2.domain.auth.dto.PasswordResetRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface AuthApi {

  @Operation(summary = "회원가입", description = "새로운 사용자를 등록하고 프로필 이미지를 업로드합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "입력값 유효성 검증 실패 (중복된 이메일, 닉네임 등)",
          content = @Content(examples = @ExampleObject(value = "User with email or username already exists"))
      ),
  })
  @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserDto> signup(
      @Parameter(
          name = "userCreateRequest",
          description = "User 생성 정보",
          required = true,
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = UserCreateRequest.class))
      ) @RequestPart UserCreateRequest userCreateRequest,
      @Parameter(
          name = "profile",
          description = "User 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "Access Token 재발급", description = "HttpOnly 쿠키에 담긴 Refresh Token을 사용하여 "
      + "새로운 Access Token을 발급받습니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Access Token 재발급 성공",
          content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"),
            examples = @ExampleObject(value = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2..."))
      ),
      @ApiResponse(
          responseCode = "400", description = "Refresh Token이 쿠키에 없거나 유효하지 않음",
          content = @Content(examples = @ExampleObject(value = "Invalid refresh token"))
      )
  })
  @PostMapping("/auth/refresh")
  ResponseEntity<String> refresh(
      @Parameter(
          name = "refreshToken",
          description = "Access Token 재발급을 위한 Refresh Token",
          in = ParameterIn.COOKIE, // 파라미터가 쿠키에 있음
          required = true,
          schema = @Schema(type = "string")
      ) @CookieValue(name = "refreshToken", required = false) String refreshToken,
      @Parameter(hidden = true) HttpServletResponse response
  );

  @Operation(summary = "비밀번호 재설정 토큰 요청", description = "사용자 이메일로 비밀번호 재설정 토큰을 발송합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "요청 성공(이메일 존재 여부와 무관하게 항상 200을 반환하여 사용자 정보 노출 방지"),
      @ApiResponse(responseCode = "400", description = "입력된 이메일 형식이 올바르지 않음"  )
  })
  @PostMapping("/auth/forgot-password")
  ResponseEntity<Void> forgotPassword(@Valid @RequestBody PasswordForgotRequest request);

  @Operation(summary = "비밀번호 재설정", description = "유효한 토큰을 사용하여 새 비밀번호로 변경합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공"),
      @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않음"),
      @ApiResponse(responseCode = "404", description = "토큰이 유효하지 않거나 만료됨")
  })
  @PostMapping("/auth/reset-password")
  ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request);
}
