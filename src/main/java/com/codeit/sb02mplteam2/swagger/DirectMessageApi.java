package com.codeit.sb02mplteam2.swagger;

import com.codeit.sb02mplteam2.domain.social.dto.FollowRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "DirectMessage", description = "DirectMessage API")
public interface DirectMessageApi {

  @Operation(summary = "DM 채널 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공"
      )
  })
  @GetMapping("/api/channels")
  ResponseEntity<Void> findAll(
      @RequestParam Long userId
  );


}
