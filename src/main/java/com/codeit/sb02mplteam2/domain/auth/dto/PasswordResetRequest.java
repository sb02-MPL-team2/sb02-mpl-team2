package com.codeit.sb02mplteam2.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
    @NotBlank(message = "토큰은 필수입니다.")
    String token,

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Size(min = 8, max = 60, message = "비밀번호는 8자 이상 60자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,}$",
        message = "비밀번호는 최소 8자 이상, 숫자, 문자, 특수문자를 포함해야 합니다.")
    String newPassword
) {

}
