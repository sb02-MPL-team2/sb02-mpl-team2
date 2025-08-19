package com.codeit.sb02mplteam2.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.social.repository.FollowRepository;
import com.codeit.sb02mplteam2.domain.user.dto.UserCursorPageResponse;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchFilter;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchRequest;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

  @Autowired
  private BasicUserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FollowRepository followRepository;

  private User currentUser;
  private List<User> otherUsers = new ArrayList<>();

  @BeforeEach
  void setUp() {
    // 1. 데스트 데이터 생성
    currentUser = userRepository.save(new User("currentUser", "current@test.com",
        "password", null));

    for(int i = 1; i <= 20; i++) {
      User other = userRepository.save(new User("testUser" + i,
          "other" + i + "@test.com", "password", null));
      otherUsers.add(other);

      // 팔로우 관계 설정: currentUser가 짝수 번호 유저만 팔로우
      if(i % 2 == 0) {
        followRepository.save(Follow.of(currentUser, other));
      }
    }

    userRepository.flush();
  }

  @Test
  @DisplayName("첫 페이지 조회: 모든 유저를 대상으로 5명 조회 시, hasNext는 true이고 nextCursor가 존재해야 한다.")
  void searchUsers_firstPage_allUsers() {
    // given
    int pageSize = 5;
    UserSearchRequest request = new UserSearchRequest(null, UserSearchFilter.ALL,
        null, pageSize);

    // when
    UserCursorPageResponse<UserSearchDto> response = userService.searchUsers(currentUser.getId(), request);

    // then
    assertThat(response.items()).hasSize(pageSize);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isNotNull();

    // isFollow 필드가 정확한지 검증 (최신 유저부터 오므로 ID가 높은 짝수 유저는 isFollow=true)
    UserSearchDto firstResultUser = response.items().get(0); // testUser20
    UserSearchDto secondResultUser = response.items().get(1); // testUser19
    assertThat(firstResultUser.isFollow()).isTrue();
    assertThat(secondResultUser.isFollow()).isFalse();
  }

  @Test
  @DisplayName("팔로우 필터링: 내가 팔로우한 유저만 조회되어야 한다.")
  void searchUsers_filterByFollowing() {
    //given
    int pageSize = 5;
    UserSearchRequest request = new UserSearchRequest(null, UserSearchFilter.FOLLOWING,
        null, pageSize);

    //when
    UserCursorPageResponse<UserSearchDto> response = userService.searchUsers(currentUser.getId(), request);

    //then
    // currentUser는 10명(짝수)을 팔로우하고 있으므로, 5명 조회 시 다음 페이지가 있어야 함
    assertThat(response.items()).hasSize(pageSize);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isNotNull();
    // 조회된 모든 유저는 isFollow가 true여야 함
    response.items().forEach(userDto -> assertThat(userDto.isFollow()).isTrue());
  }

}
