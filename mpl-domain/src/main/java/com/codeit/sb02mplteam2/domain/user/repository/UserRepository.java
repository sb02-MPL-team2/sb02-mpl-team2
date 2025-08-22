package com.codeit.sb02mplteam2.domain.user.repository;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);

  @Query("select u from User u left join fetch u.playlists left join fetch u.profile where u.id = :id")
  Optional<User> findById(Long id);

  @Query("select u from User u left join fetch u.profile where u.email = :email")
  Optional<User> findByEmailWithProfile(@Param("email") String email);

  Set<Long> findAllIds();
}
