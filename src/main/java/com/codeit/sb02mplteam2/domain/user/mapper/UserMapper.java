package com.codeit.sb02mplteam2.domain.user.mapper;


import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // UserMapperImpl 클래스에 Spring의 @Component 애노테이션을 추가
public interface UserMapper {

  @Mapping(source = "user.id", target = "id")
  @Mapping(source = "user.username", target = "username")
  @Mapping(
      target = "profileUrl",
      expression = "java(user.getProfile() != null ? user.getProfile().getUrl() : null)"
  )
  UserDto toDto(User user, int followerCount, int followingCount);
}
