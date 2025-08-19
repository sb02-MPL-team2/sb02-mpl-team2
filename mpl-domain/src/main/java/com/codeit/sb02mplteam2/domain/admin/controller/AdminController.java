package com.codeit.sb02mplteam2.domain.admin.controller;

import com.codeit.sb02mplteam2.domain.admin.service.AdminService;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminService adminService;

  @PutMapping("/users/{userId}/role")
  public ResponseEntity<UserDto> updateUserRole(
      @PathVariable Long userId,
      @RequestBody RoleUpdateRequest request
  )
  {
    log.info("사용자 권한 수정 요청: {}, {}", userId, request);
    UserDto response = adminService.updateUserRole(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  @PostMapping("/users/{userId}/lock")
  public ResponseEntity<Void> lockUser(@PathVariable Long userId){
    log.info("사용자 잠금 요청: {}", userId);
    adminService.lockUser(userId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/users/{userId}/unlock")
  public ResponseEntity<Void> unlockUser(@PathVariable Long userId){
    log.info("사용자 잠금 해제 요청: {}", userId);
    adminService.unlockUser(userId);
    return ResponseEntity.ok().build();
  }
}
