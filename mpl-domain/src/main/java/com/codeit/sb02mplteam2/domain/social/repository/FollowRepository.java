package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

  // follower 계산
  int countByToUser(User toUser);

  // following 계산
  int countByFromUser(User fromUser);

  boolean existsByToUserIdAndFromUserId(Long followeeId, Long followerId);

  Optional<Follow> findByToUserIdAndFromUserId(Long followeeId, Long followerId);

  @Query("""
        SELECT f
        FROM Follow f
        JOIN FETCH f.fromUser u
        WHERE f.toUser.id = :userId
        AND (:cursor IS NULL OR f.id < :cursor)
        ORDER BY f.id DESC
    """)
  List<Follow> findFollowers(
      @Param("userId") Long userId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );

  @Query("""
        SELECT f
        FROM Follow f
        JOIN FETCH f.toUser u
        WHERE f.fromUser.id = :userId
        AND (:cursor IS NULL OR f.id < :cursor)
        ORDER BY f.id DESC
    """)
  List<Follow> findFollowings(
      @Param("userId") Long userId,
      @Param("cursor") Long cursor,
      Pageable pageable
  );

  @Query("SELECT DISTINCT f.fromUser.id FROM Follow f WHERE f.toUser.id = :userId")
  Set<Long> findAllFollowersIdByToUserId(Long userId);
}
