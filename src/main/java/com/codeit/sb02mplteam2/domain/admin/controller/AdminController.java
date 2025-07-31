package com.codeit.sb02mplteam2.domain.admin.controller;

import com.codeit.sb02mplteam2.domain.admin.service.AdminService;
import com.codeit.sb02mplteam2.domain.user.dto.RoleUpdateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("updateUserRole/{userId}")
  ResponseEntity<UserDto> updateUserRole(
      @PathVariable Long userId,
      @RequestBody RoleUpdateRequest request
  )
  {
    UserDto response = adminService.updateUserRole(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }
}
