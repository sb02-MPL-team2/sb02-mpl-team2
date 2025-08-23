package com.codeit.sb02mplteam2.domain.task;

import com.codeit.sb02mplteam2.util.RabbitConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskRabbitMQConfig {

  @Bean
  public Queue taskQueue() {
    return new Queue(RabbitConst.taskQueue, false);
  }

  @Bean
  public Queue taskBulkQueue() {
    return new Queue(RabbitConst.taskBulkQueue, false);
  }

  @Bean
  public TopicExchange taskExchange() {
    return new TopicExchange(RabbitConst.taskExchange);
  }

  @Bean
  public Binding binding(Queue taskQueue, TopicExchange taskExchange) {
    return BindingBuilder
        .bind(taskQueue)
        .to(taskExchange)
        .with(RabbitConst.taskNotificationCreateRoutingKey);
  }

  @Bean
  public Binding bindingBulk(Queue taskBulkQueue, TopicExchange taskExchange) {
    return BindingBuilder
        .bind(taskBulkQueue)
        .to(taskExchange)
        .with(RabbitConst.taskNotificationBulkCreateRoutingKey);
  }
}
