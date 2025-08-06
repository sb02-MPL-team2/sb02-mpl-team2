package com.codeit.sb02mplteam2.domain.user.repository;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);

  @Query("select u from User u left join fetch u.playlists left join u.profile where u.id = :id")
  Optional<User> findById(Long id);
}
