package com.codeit.sb02mplteam2.domain.notification.service;

import static org.mockito.Mockito.verify;

import com.codeit.sb02mplteam2.domain.notification.repository.NotificationRepository;
import com.codeit.sb02mplteam2.domain.playlist.repository.PlaylistRepository;
import com.codeit.sb02mplteam2.domain.setting.entity.AlarmSetting;
import com.codeit.sb02mplteam2.domain.setting.repository.AlarmSettingRepository;
import com.codeit.sb02mplteam2.domain.user.entity.User;
import com.codeit.sb02mplteam2.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private AlarmSettingRepository alarmSettingRepository;

  @Mock
  private PlaylistRepository playlistRepository;

  @InjectMocks
  private BasicNotificationService notificationService;

  private User receiver;
  private User publisher;
  private AlarmSetting alarmSetting;

  @BeforeEach
  void setUp() {
    receiver = new User();
    publisher = new User();
    alarmSetting = new AlarmSetting(receiver);
  }

  @Test
  void delete() {
    Long notificationId = 1L;
    notificationService.delete(notificationId);
    verify(notificationRepository).deleteById(notificationId);
  }

  @Test
  void deleteAllByUserId() {
    Long userId = 1L;
    notificationService.deleteAllByUserId(userId);
    verify(notificationRepository).deleteAllByReceiverId(userId);
  }

//  @Test
//  @Disabled
//  void create() {
//    NotificationType notificationType = NotificationType.NEW_FOLLOWER;
//    Long receiverId = 1L;
//    Long publisherId = 2L;
//    Long targetId = 1L;
//
//    when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
//    when(userRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
//    when(alarmSettingRepository.findByUser(receiver)).thenReturn(Optional.of(alarmSetting));
//
//    NotificationDto notificationDto = notificationService.create(receiverId, notificationType,
//        targetId, publisherId);
//
//    assertAll(
//        () -> assertNotNull(notificationDto),
//        () -> assertEquals(receiverId, notificationDto.receiverId()),
//        () -> assertEquals(publisherId, notificationDto.publisherId())
//    );
//  }

//  @Test
//  @Disabled
//  void createAll() {
//    // given
//    NotificationType notificationType = NotificationType.NEW_FOLLOWER;
//    Long publisherId = 1L;
//    Long receiverId1 = 2L;
//    Long receiverId2 = 3L;
//    Long targetId = 1L;
//    Set<Long> receiverIds = Set.of(receiverId1, receiverId2);
//
//    // 테스트 객체 생성 및 ID 설정
//    ReflectionTestUtils.setField(publisher, "id", publisherId);
//
//    User receiver1 = new User();
//    ReflectionTestUtils.setField(receiver1, "id", receiverId1);
//    User receiver2 = new User();
//    ReflectionTestUtils.setField(receiver2, "id", receiverId2);
//    List<User> receivers = List.of(receiver1, receiver2);
//
//    AlarmSetting alarmSetting1 = new AlarmSetting(receiver1);
//    alarmSetting1.setFollowAlarmEnabled(true); // 알림 켜기
//    AlarmSetting alarmSetting2 = new AlarmSetting(receiver2);
//    alarmSetting2.setFollowAlarmEnabled(true); // 알림 켜기
//    List<AlarmSetting> alarmSettings = List.of(alarmSetting1, alarmSetting2);
//
//    // Mock 객체의 동작 정의
//    when(userRepository.findById(publisherId)).thenReturn(Optional.of(publisher));
//    when(userRepository.findAllById(receiverIds)).thenReturn(receivers);
//    when(alarmSettingRepository.findAllByUserIn(receivers)).thenReturn(alarmSettings);
//    // *** 중요: saveAll 메서드가 호출되면, 인자로 받은 리스트를 그대로 반환하도록 설정
//    when(notificationRepository.saveAll(any(List.class))).thenAnswer(
//        invocation -> invocation.getArgument(0));
//
//    // when
//    List<NotificationDto> notificationDtoList = notificationService.createAll(receiverIds,
//        notificationType, targetId, publisherId);
//
//    // then
//    assertAll(
//        () -> assertEquals(2, notificationDtoList.size()),
//        // 순서에 상관없이 ID가 모두 포함되어 있는지 확인
//        () -> {
//          Set<Long> resultReceiverIds = notificationDtoList.stream()
//              .map(NotificationDto::receiverId)
//              .collect(Collectors.toSet());
//          assertEquals(receiverIds, resultReceiverIds);
//        },
//        // 모든 알림의 publisherId가 동일한지 확인
//        () -> notificationDtoList.forEach(dto -> assertEquals(publisherId, dto.publisherId()))
//    );
//  }
}