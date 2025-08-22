package com.codeit.sb02mplteam2.domain.task;

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
    return new Queue("task.queue", false);
  }

  @Bean
  public TopicExchange taskExchange() {
    return new TopicExchange("task.exchange");
  }

  @Bean
  public Binding binding(Queue taskQueue, TopicExchange taskExchange) {
    return BindingBuilder
        .bind(taskQueue)
        .to(taskExchange)
        .with("order.*");
  }
}
