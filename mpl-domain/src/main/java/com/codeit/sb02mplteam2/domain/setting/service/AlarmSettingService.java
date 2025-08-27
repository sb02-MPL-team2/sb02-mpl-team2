package com.codeit.sb02mplteam2.domain.setting.service;

import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.setting.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.exception.ErrorCode;
import com.codeit.sb02mplteam2.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmSettingService {

  private final AlarmSettingRepository alarmSettingRepository;

  @Cacheable(value = "alarms", key = "#userId")
  public AlarmSetting findByUserId(Long userId) {
    return alarmSettingRepository.findByUserId(userId)
        .orElseThrow(() -> new UserException(
            ErrorCode.ALARM_NOT_FOUND));
  }

  @CachePut(value = "alarms", key = "#userId")
  public AlarmSetting refreshAndFindById(Long userId) {
    return alarmSettingRepository.findByUserId(userId)
        .orElseThrow(() -> new UserException(
            ErrorCode.ALARM_NOT_FOUND));
  }
}
