package com.codeit.sb02mplteam2.domain.user.dto;

public record UserCreateRequest(
    String username,
    String email,
    String password
) {

}
