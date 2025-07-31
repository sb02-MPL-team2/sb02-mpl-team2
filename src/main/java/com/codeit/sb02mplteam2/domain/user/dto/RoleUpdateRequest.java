package com.codeit.sb02mplteam2.domain.user.dto;

import com.codeit.sb02mplteam2.domain.user.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record RoleUpdateRequest(
    @NotNull(message = "권한을 입력해주세요.")
    Role role
) {

}
