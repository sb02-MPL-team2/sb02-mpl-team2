package com.codeit.sb02mplteam2.domain.auth.service;

import com.codeit.sb02mplteam2.domain.user.dto.UserDto;
import com.codeit.sb02mplteam2.domain.user.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.user.entity.Role;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.mapper.UserMapper;
import com.codeit.sb02mplteam2.domain.user.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  @Value("${mpl.admin.username}")
  private String username;
  @Value("${mpl.admin.email}")
  private String email;
  @Value("${mpl.admin.password}")
  private String password;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AlarmSettingRepository alarmSettingRepository;
  // 서버가 실행되면 initAdmin() 실행 -> admin이 없는 경우 admin 등록
  @Transactional
  @Override
  public UserDto initAdmin() {
    if(userRepository.existsByEmail(email) || userRepository.existsByUsername(username)){
      log.warn("이미 어드민이 존재합니다.");
      return null;
    }

    String encodedPassword = passwordEncoder.encode(password);
    User admin = new User(username, email, encodedPassword, null);
    admin.updateRole(Role.ADMIN);
    userRepository.save(admin);
    alarmSettingRepository.save(new AlarmSetting(admin));
    UserDto adminDto = userMapper.toDto(admin);
    log.info("어드민이 초기화되었습니다. {}", adminDto);
    return adminDto;
  }
}
