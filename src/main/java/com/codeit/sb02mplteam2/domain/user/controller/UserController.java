package com.codeit.sb02mplteam2.domain.user.controller;

import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.service.UserService;
import com.codeit.sb02mplteam2.security.MplUserDetails;
import com.codeit.sb02mplteam2.security.jwt.CheckJwtBlacklist;
import com.codeit.sb02mplteam2.swagger.UserApi;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  @CheckJwtBlacklist
  @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
  @PutMapping(value ="/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserDto> update(
      @PathVariable("userId") Long userId,
      @RequestPart("userUpdateRequest") @Valid UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile
  ) {
    log.info("사용자 수정 요청: {}, {}", userId, userUpdateRequest);
    UserDto userDto = userService.update(userId, userUpdateRequest, Optional.ofNullable(profile));
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable Long userId) {
    log.info("유저 조회 요청: {}", userId);
    UserDto user = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(user);
  }

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/users")
  public ResponseEntity<List<UserDto>> findAll() {
    log.info("유저 목록 조회 요청");
    List<UserDto> users = userService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(users);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<Void> delete(@PathVariable Long userId) {
    log.info("사용자 삭제 요청: {}", userId);
    userService.softDelete(userId);
    return ResponseEntity.noContent().build();
  }

  @CheckJwtBlacklist
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/users/me")
  public ResponseEntity<UserDto> getMyInfo(@AuthenticationPrincipal MplUserDetails userDetails) {
    Long myId = userDetails.getId();
    log.info("내 정보 조회 요청: {}", myId);
    UserDto userDto = userService.findById(myId);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @CheckJwtBlacklist
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/users/me")
  public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal MplUserDetails userDetails) {
    Long myId = userDetails.getId();
    log.info("내 계정 삭제 요청: {}", myId);
    userService.softDelete(myId);
    return ResponseEntity.noContent().build();
  }
}
