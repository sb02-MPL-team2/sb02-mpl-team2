package com.codeit.sb02mplteam2.domain.review.repository;

import com.codeit.sb02mplteam2.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  @Query("SELECT r FROM Review r "
      + "LEFT JOIN fetch r.content "
      + "LEFT JOIN fetch r.user u "
      + "WHERE u.id = :userId")
  List<Review> findAllByUserId(Long userId);

  @Query("SELECT r FROM Review r "
      + "LEFT JOIN fetch r.user "
      + "LEFT JOIN fetch r.content c "
      + "WHERE c.id = :contentId")
  List<Review> findAllByContentId(Long contentId);
}
