package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import com.codeit.sb02mplteam2.security.MplUserDetails;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
public interface UserApi {

  @Operation(summary = "User 등록")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "User가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 을 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject(value = "User with email already exists"))
      ),
  })
  @PostMapping(value = "/api/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserDto> create(
      @Parameter(
          description = "User 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ) @RequestPart UserCreateRequest userCreateRequest,
      @Parameter(
          description = "User 프로필 이미지",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
      ) @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 정보 수정")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 email 또는 username를 사용하는 User가 이미 존재함",
          content = @Content(examples = @ExampleObject("user with email {newEmail} already exists"))
      )
  })
  @PutMapping(value ="/api/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<UserDto> update(
      @Parameter(description = "수정할 User ID") @PathVariable Long userId,
      @Parameter(description = "수정할 User 정보") @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @Parameter(description = "수정할 User 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
  );

  @Operation(summary = "User 삭제")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "User가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {id} not found"))
      )
  })
  @DeleteMapping("/api/users/{userId}")
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 User ID") @PathVariable Long userId
  );

  @Operation(summary = "전체 User 목록 조회 (관리자용)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
      )
  })
  @GetMapping("/api/users")
  ResponseEntity<List<UserDto>> findAll();

  @Operation(summary = "특정 User 조회 (관리자용)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "User 조회 성공",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "User를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))
      )
  })
  @GetMapping("/api/users/{userId}")
  ResponseEntity<UserDto> findById(@Parameter(description = "찾고 싶은 User ID",
      required = true, example = "1") @PathVariable Long userId);

  @Operation(summary = "내 정보 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  @GetMapping("/api/users/me")
  ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal MplUserDetails userDetails);

  @Operation(summary = "회원 탈퇴 (내 계정 삭제)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  @DeleteMapping("/api/users/me")
  ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal MplUserDetails userDetails);
}
