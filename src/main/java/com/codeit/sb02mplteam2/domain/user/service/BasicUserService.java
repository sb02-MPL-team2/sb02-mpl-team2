package com.codeit.sb02mplteam2.domain.user.service;

import com.codeit.sb02mplteam2.domain.binary.entity.BinaryContent;
import com.codeit.sb02mplteam2.domain.user.dto.UserCreateRequest;
import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserUpdateRequest;
import java.util.List;
import java.util.Optional;

public class BasicUserService implements UserService{
  public UserDto create(UserCreateRequest request, Optional<BinaryContent> profileCreateRequest){
    return null;
  }

  public UserDto find(Long userId){
    return null;
  }

  public List<UserDto> findAll(){
    return null;
  }

  public UserDto update(Long userId, UserUpdateRequest request,
      Optional<BinaryContent> profileCreateRequest){
    return null;
  }

  public void delete(Long userId){

  }
}
