package com.codeit.sb02mplteam2.domain.notification;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitMQConfig {
  //메시지 큐
  @Bean
  public Queue notificationQueue() {
    return new Queue("notification.queue", false); // durable: 서버 재시작 시 유지할지 여부
  }

  @Bean
  public Queue notificationLogQueue() {
    return new Queue("system.notification.log.queue", false);
  }

  //교환소
  @Bean
  public TopicExchange notificationExchange() {
    return new TopicExchange("notification.exchange");
  }

  //라우팅키를 바탕으로 메시지큐 할당함
  @Bean
  public Binding bindingPlaylist(Queue notificationQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationQueue)
        .to(notificationExchange)
        .with("notification.playlist.*");
  }

  @Bean
  public Binding bindingUserRole(Queue notificationQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationQueue)
        .to(notificationExchange)
        .with("notification.user.role.changed");
  }

  @Bean
  public Binding bindingAllToLog(Queue notificationLogQueue, TopicExchange notificationExchange) {
    return BindingBuilder
        .bind(notificationLogQueue)
        .to(notificationExchange)
        .with("notification.#");
  }
}
