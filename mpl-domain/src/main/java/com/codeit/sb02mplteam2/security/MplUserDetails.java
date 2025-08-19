package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class MplUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  public Long getId() {
    return userDto.id();
  }

  @Override
  public String getUsername() {
    return userDto.email();
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  /*
  * 계정 잠금 여부, user.isLocked() 값을 반환
  * isLocked가 true이면 계정이 잠긴 것, non-locked는 false
  * 계정 잠겨 있으면 LockedException 발생 시킨 후 인증 중단
  * */
  @Override
  public boolean isAccountNonLocked() {
    return !userDto.isLocked();
  }

  @Override
  public boolean isEnabled() {
    return !userDto.isDeleted();
  }
}
