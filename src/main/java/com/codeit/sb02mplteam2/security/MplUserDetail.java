package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class MplUserDetail implements UserDetails {

  private final User user;

  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public String getPassword() {
    // Todo custom Login 사용하게 되면 교체
//    return user.getEmail();
    return user.getPassword();
  }

  /*
  * 계정 잠금 여부, user.isLocked() 값을 반환
  * isLocked가 true이면 계정이 잠긴 것, non-locked는 false
  * */
  @Override
  public boolean isAccountNonLocked() {
    return !user.isLocked();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return !user.isDeleted();
  }

  public User getUser(){
    return user;
  }
}
