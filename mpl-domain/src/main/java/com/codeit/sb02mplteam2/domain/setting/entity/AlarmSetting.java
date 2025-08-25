package com.codeit.sb02mplteam2.domain.setting.entity;

import com.codeit.sb02mplteam2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** <h2>유저의 알람 셋팅 엔티티</h2><br>
 * 해당 엔티티를 확인해 알람을 전송할 지, 전송하지 않을 지 결정할 수 있습니다.
 */
@Entity
@NoArgsConstructor
@Getter
public class AlarmSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  // 팔로우 알람 설정
  @Setter
  @Column(nullable = false)
  private Boolean followAlarmEnabled = true;

  // 권한 변경 알람 설정
  @Setter
  @Column(nullable = false)
  private Boolean permissionChangeAlarmEnabled = true;

  // 팔로잉의 새 재생목록 알람 설정
  @Setter
  @Column(nullable = false)
  private Boolean newPlaylistFromFollowingAlarmEnabled = true;

  @Setter
  @Column(nullable = false)
  private Boolean subscribePlaylistAlarmEnable = true;

  // DM 알람 설정
  @Setter
  @Column(nullable = false)
  private Boolean dmAlarmEnabled = true;

  @Setter
  @Column(nullable = false)
  private Boolean recommendPlaylistAlarmEnabled = true;

  public AlarmSetting(User user) {
    this.user = user;
  }

  private Boolean updatedField(Boolean newValue, Boolean oldValue) {
    if (newValue != null && newValue != oldValue) {
      return newValue;
    }
    return oldValue;
  }

  public void update(Boolean followAlarmEnabled,
      Boolean permissionChangeAlarmEnabled,
      Boolean newPlaylistFromFollowingAlarmEnabled,
      Boolean dmAlarmEnabled,
      Boolean subscribePlaylistAlarmEnable,
      Boolean recommendPlaylistAlarmEnabled) {
    this.followAlarmEnabled =
        updatedField(followAlarmEnabled, this.followAlarmEnabled);
    this.permissionChangeAlarmEnabled =
        updatedField(permissionChangeAlarmEnabled, this.permissionChangeAlarmEnabled);
    this.newPlaylistFromFollowingAlarmEnabled =
        updatedField(newPlaylistFromFollowingAlarmEnabled, this.newPlaylistFromFollowingAlarmEnabled);
    this.dmAlarmEnabled =
        updatedField(dmAlarmEnabled, this.dmAlarmEnabled);
    this.subscribePlaylistAlarmEnable =
        updatedField(subscribePlaylistAlarmEnable, this.subscribePlaylistAlarmEnable);
    this.recommendPlaylistAlarmEnabled =
        updatedField(recommendPlaylistAlarmEnabled, this.recommendPlaylistAlarmEnabled);

  }
}
