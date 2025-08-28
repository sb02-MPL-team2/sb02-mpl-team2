package com.codeit.sb02mplteam2.domain.user.mapper;


import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // UserMapperImpl 클래스에 Spring의 @Component 애노테이션을 추가
public interface UserMapper {

  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  default UserDto toDto(User user) {
    // user 객체가 null일 경우를 대비한 방어 코드
    if (user == null) {
      return null;
    }

    // 프로필 이미지가 없을 경우 null을 반환하도록 처리
    String profileUrl = user.getProfile() != null ? user.getProfile().getUrl() : user.getPictureUrl();

    // UserDto record의 생성자를 사용하여 직접 객체를 생성하고 반환
    return new UserDto(
        user.getId(),
        user.getEmail(),
        user.getUsername(),
        profileUrl,
        user.getRole(),
        formatter.format(user.getCreatedAt()),
        user.isLocked(),
        user.isDeleted(),
        user.getFollowerCount(),
        user.getFollowingCount()
    );
  }
}
