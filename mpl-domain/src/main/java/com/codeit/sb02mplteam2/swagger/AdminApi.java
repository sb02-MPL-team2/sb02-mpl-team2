package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Admin", description = "Admin API")
@SecurityRequirement(name = "bearerAuth")
public interface AdminApi {

  @Operation(summary = "사용자 권한 변경",
      description = "특정 사용자의 권한을 ADMIN 또는 USER로 변경합니다. (관리자 권한 필요) "
          + "권한이 변경된 사용자는 자동으로 로그아웃 처리됩니다.")
  @ApiResponses( value = {
      @ApiResponse(
          responseCode = "200", description = "권한이 성공적으로 변경됨",
          content = @Content(schema = @Schema(implementation = UserDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 권한 요청",
          content = @Content(examples = @ExampleObject(value = "Invalid role request"))
      ),
      @ApiResponse(
          responseCode = "403", description = "권한 없음"
      )
  })
  ResponseEntity<UserDto> updateUserRole(
      @Parameter(description = "권한을 변경할 사용자 ID", required = true, example = "2") Long userId,
      @Parameter(description = "변경할 권한 정보", required = true)RoleUpdateRequest request
  );

  @Operation( summary = "사용자 계정 잠금",
      description = "특정 사용자 계정을 잠금 처리합니다. 잠긴 계정은 로그인 할 수 없으며, 강제로 로그아웃 됩니다."
          + " (관리자 권한 필요)")
  @ApiResponses( value = {
      @ApiResponse(
          responseCode = "204", description = "사용자 계정이 성공적으로 잠금 처리됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "이미 잠긴 계정",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} is already locked"))
      ),
      @ApiResponse(
          responseCode = "403", description = "권한 없음"
      )
  })
  ResponseEntity<Void> lockUser(
      @Parameter(description = "잠금 처리할 사용자 ID", required = true, example = "2") Long userId);

  @Operation( summary = "사용자 계정 잠금 해제",
      description = "특정 사용자 계정의 잠금을 해제합니다. (관리자 권한 필요)")
  @ApiResponses( value = {
      @ApiResponse(
          responseCode = "200", description = "계정 잠금이 성공적으로 해제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} not found"))
      ),
      @ApiResponse(
          responseCode = "409", description = "이미 잠금 해제된 계정",
          content = @Content(examples = @ExampleObject(value = "User with id {userId} is already unlocked"))
      ),
      @ApiResponse(
          responseCode = "403", description = "권한 없음"
      )
  })
  ResponseEntity<Void> unlockUser(
      @Parameter(description = "잠금 해제할 사용자 ID", required = true, example = "2") Long userId);
}
