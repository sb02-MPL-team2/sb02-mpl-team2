package com.codeit.sb02mplteam2.domain.mail;

public record PasswordResetEvent(
    String email,
    String resetLink
) {

}
