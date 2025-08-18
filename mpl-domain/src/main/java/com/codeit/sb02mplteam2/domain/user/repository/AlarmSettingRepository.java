package com.codeit.sb02mplteam2.domain.user.repository;

import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {

  Optional<AlarmSetting> findByUser(User user);

  List<AlarmSetting> findAllByUserIn(List<User> users);

  @Query("SELECT a FROM AlarmSetting a WHERE a.user.id = :userId")
  Optional<AlarmSetting> findByUserId(Long userId);
}
