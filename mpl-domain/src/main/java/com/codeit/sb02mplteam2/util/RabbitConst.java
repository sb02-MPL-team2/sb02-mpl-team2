package com.codeit.sb02mplteam2.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitConst {
  //Notification RabbitMQ
  public static final String notificationExchange = "notification.exchange";
  public static final String notificationQueue = "notification.queue";
  public static final String notificationBulkQueue = "notification.bulk.queue";

  public static final String notificationReceiveQueue = "notification.send.queue";
  public static final String notificationReceiveBulkQueue = "notification.send.bulk.queue";

  //DLX
  public static final String DEAD_LETTER_EXCHANGE = "dlx.exchange";
  public static final String NOTIFICATION_DEAD_LETTER_QUEUE = "notification.queue.dlq";


  //SSE 미확인된 알람 처리를 위한 라우팅 키
  public static final String LostEvent_Send_Notification_RoutingKey = "notification.lost-event";
  //플레이리스트 생성 SSE 발송을 위한 라우팅 키
  public static final String Playlist_Send_Notification_RoutingKey = "notification.playlist";
  //유저 권한 변경을 위한 라우팅 키
  public static final String UserRoleChanged_Send_Notification_RoutingKey = "notification.user";
  //DM 전송을 위한 라우팅 키
  public static final String DirectMessage_Send_Notification_RoutingKey = "notification.direct-messages";
  //대량 전송을 위한 라우팅 키
  public static final String Notification_Bulk_Send_RoutingKey = "notification.bulk";
  //만들어진 알람 수신을 위한 라우팅 키
  public static final String Notification_Receive_RoutingKey = "notification.receive";
  //만들어진 대량 알람 수신을 위한 라우팅 키
  public static final String Notification_Bulk_Receive_RoutingKey = "notification.receive.bulk";

  //Task RabbitMQ
  public static final String taskExchange = "task.exchange";
  public static final String taskQueue = "task.queue";
  public static final String taskBulkQueue = "task.bulk.queue";
  public static final String taskLogQueue = "task.log.queue";

  //알람 생성을 위한 라우팅 키
  public static final String taskNotificationCreateRoutingKey = "task.notification";

  //알람 대량 생성을 위한 라우팅 키
  public static final String taskNotificationBulkCreateRoutingKey = "task.notification.bulk";













}
