package com.codeit.sb02mplteam2.security;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class MplUserDetails implements UserDetails, OAuth2User {

  private final UserDto userDto;
  private final String password;
  private final Map<String, Object> attributes;


  private MplUserDetails(UserDto userDto, String password, Map<String, Object> attribute) {
    this.userDto = userDto;
    this.attributes = attribute;
    this.password = password;
  }

  public static MplUserDetails forLocalUser(UserDto userDto, String password) {
    return new MplUserDetails(userDto, password, Map.of());
  }

  public static MplUserDetails forSocialUser(UserDto userDto, Map<String, Object> attribute) {
    return new MplUserDetails(userDto, UUID.randomUUID().toString(), attribute);
  }

//  ----- OAuth2User -----

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public String getName() {
    return String.valueOf(userDto.id());
  }

//   ----- UserDetails -----

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
