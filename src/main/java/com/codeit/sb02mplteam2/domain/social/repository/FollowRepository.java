package com.codeit.sb02mplteam2.domain.social.repository;

import com.codeit.sb02mplteam2.domain.social.entity.Follow;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        AND (:cursor IS NULL OR f.createdAt < :cursor)
        ORDER BY f.createdAt DESC
        """)
  List<Follow> findFollowersWithCursor(
      @Param("userId") Long userId,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable
  );

  @Query("""
        SELECT f 
        FROM Follow f
        JOIN FETCH f.toUser u
        WHERE f.fromUser.id = :userId
        AND (:cursor IS NULL OR f.createdAt < :cursor)
        ORDER BY f.createdAt DESC
        """)
  List<Follow> findFollowingsWithCursor(
      @Param("userId")Long userId,
      @Param("cursor") LocalDateTime cursor,
      Pageable pageable
  );
}
