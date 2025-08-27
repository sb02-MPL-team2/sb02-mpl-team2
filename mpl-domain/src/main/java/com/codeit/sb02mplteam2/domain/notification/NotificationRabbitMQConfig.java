package com.codeit.sb02mplteam2.domain.notification;

import com.codeit.sb02mplteam2.util.RabbitConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NotificationRabbitMQConfig {
  //메시지 큐
  @Bean
  public Queue notificationQueue() {
    // durable: 서버 재시작 시 유지할지 여부
    return QueueBuilder.nonDurable(RabbitConst.notificationQueue)
        .withArgument("x-dead-letter-exchange", RabbitConst.DEAD_LETTER_EXCHANGE)
        .build();
  }

  @Bean
  public Queue notificationBulkQueue() {
    return QueueBuilder.nonDurable(RabbitConst.notificationBulkQueue)
        .withArgument("x-dead-letter-exchange", RabbitConst.DEAD_LETTER_EXCHANGE)
        .build();
  }

  @Bean
  public Queue notificationReceiveQueue() {
    return new Queue(RabbitConst.notificationReceiveQueue, false);
  }

  @Bean
  public Queue notifiactionReceiveBulkQueue() {
    return new Queue(RabbitConst.notificationReceiveBulkQueue, false);
  }

  //교환소
  @Bean
  public TopicExchange notificationExchange() {
    return new TopicExchange(RabbitConst.notificationExchange);
  }

  //라우팅키를 바탕으로 메시지큐 할당함
  @Bean
  public Binding bindingSend(Queue notificationReceiveQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationReceiveQueue)
        .to(notificationExchange)
        .with(RabbitConst.Notification_Receive_RoutingKey);
  }

  @Bean
  public Binding bindingSendBulk(Queue notifiactionReceiveBulkQueue,
      TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notifiactionReceiveBulkQueue)
        .to(notificationExchange)
        .with(RabbitConst.Notification_Bulk_Receive_RoutingKey);
  }

  @Bean
  public Binding bindingPlaylist(Queue notificationQueue, TopicExchange notificationExchange) {
    log.info("RabbitMQ 수신 완료");
    log.info(notificationQueue.getName());
    return BindingBuilder
        .bind(notificationQueue)
        .to(notificationExchange)
        .with(RabbitConst.Playlist_Send_Notification_RoutingKey);
  }

  @Bean
  public Binding bindingUserRole(Queue notificationQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationQueue)
        .to(notificationExchange)
        .with(RabbitConst.UserRoleChanged_Send_Notification_RoutingKey);
  }

  @Bean
  public Binding bindingDirectMessage(Queue notificationQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationQueue)
        .to(notificationExchange)
        .with(RabbitConst.DirectMessage_Send_Notification_RoutingKey);
  }

  @Bean
  public Binding bindingBulkEvent(Queue notificationBulkQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationBulkQueue)
        .to(notificationExchange)
        .with(RabbitConst.Notification_Bulk_Send_RoutingKey);
  }

  @Bean
  public TopicExchange deadLetterExchange() {
    return new TopicExchange(RabbitConst.DEAD_LETTER_EXCHANGE);
  }

  @Bean
  public Queue notificationDeadLetterQueue() {
    return new Queue(RabbitConst.NOTIFICATION_DEAD_LETTER_QUEUE, false);
  }

  @Bean
  public Binding bindingDeadLetter(Queue notificationDeadLetterQueue, TopicExchange deadLetterExchange) {
    return BindingBuilder
        .bind(notificationDeadLetterQueue)
        .to(deadLetterExchange)
        .with("#"); // 모든 라우팅 키에 대해 바인딩
  }


}
