package com.codeit.sb02mplteam2.domain.user.repository;

import static com.codeit.sb02mplteam2.domain.social.entity.QFollow.follow;
import static com.codeit.sb02mplteam2.domain.user.entity.QUser.user;

import com.codeit.sb02mplteam2.domain.user.dto.UserSearchDto;
import com.codeit.sb02mplteam2.domain.user.dto.UserSearchFilter;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<UserSearchDto> searchByFilter(Long currentUserId, String keyword,
      UserSearchFilter filter, Long cursorId, int pageSize) {
    return queryFactory
        .select(Projections.constructor(UserSearchDto.class,
            user.id,
            user.username,
            user.profile.url,
            user.role,
            user.followerCount,
            JPAExpressions.selectOne()
                .from(follow)
                .where(follow.fromUser.id.eq(currentUserId)
                    .and(follow.toUser.id.eq(user.id)))
                .exists()
        ))
        .from(user)
        .where(
            cursorIdLessThan(cursorId),
            usernameContains(keyword),
            filterByFollowStatus(currentUserId, filter)
        )
        .orderBy(user.id.desc()) // 최신순
        .limit(pageSize)
        .fetch();
  }

  private BooleanExpression cursorIdLessThan(Long cursorId) {
    return cursorId != null ? user.id.lt(cursorId) : null;
  }

  private BooleanExpression usernameContains(String keyword) {
    return StringUtils.hasText(keyword) ? user.username.contains(keyword) : null;
  }

  private BooleanExpression filterByFollowStatus(Long currentUserId, UserSearchFilter filter) {
    if (filter == UserSearchFilter.FOLLOWING) {
      // 내가 팔로우 한 사람들만 필터링
      return JPAExpressions.selectFrom(follow)
          .where(follow.fromUser.id.eq(currentUserId)
              .and(follow.toUser.id.eq(user.id)))
          .exists();
    }
    if (filter == UserSearchFilter.NOT_FOLLOWING) {
      // 내가 팔로우 하지 않은 사람들만 피렅링
      return JPAExpressions.selectFrom(follow)
          .where(follow.fromUser.id.eq(currentUserId)
              .and(follow.toUser.id.eq(user.id)))
          .notExists()
          .and(user.id.ne(currentUserId)); // 자기 자신은 검색 결과에서 제외
    }

    // filter == ALL 이거나 null 이면 아무 조건도 적용하지 않음
    return null;
  }
}
